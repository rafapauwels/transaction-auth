(ns transaction-authorization.controller
  (:require 
   [transaction-authorization.validator :as validator]
   [transaction-authorization.adapter   :as adapter]
   [transaction-authorization.logic     :as logic]
   [transaction-authorization.port      :as port]))

(defn empty-body? [body]
  (or (empty? body) (nil? body)))

(defn request-transaction-auth! 
  "Requests a transaction authorization, this will store both the transaction and any violations that may happen in the db"
  [valid-transaction]
  (let [account-state       (port/account-state "internal-notation")
        latest-transactions (take 10 (port/transaction-history))
        auth-request        (logic/authorize account-state latest-transactions)]
    (if (= (:authorized auth-request) true)
      (do (let [available-limit    (-> account-state (:account) (:available-limit))
                transaction-amount (:amount valid-transaction)
                new-limit          (logic/limit-after-transaction available-limit transaction-amount)]
            (port/edit-account! :available-limit new-limit)))
      (port/add-violations! (:violations auth-request)))))

(defn return-account
  "Get the account state and any violations that happened during the authorization/creation phase"
  []
  (let [account-state     (port/account-state)
        current-violation (port/current-violation)]
    (port/stash-current-violation!)
    (adapter/map->json (conj account-state current-violation))))

(defn new-account 
  "Glues the account creation flow, validates the request, maps the json and returns an answer to the client"
[account]
  (when (not (empty-body? account))
    (let [mapped-account (adapter/json->map account)
          valid-account  (validator/account mapped-account)]
      (when (boolean mapped-account)
        (if (and (not (nil? valid-account))
                 (not (= :bad-format valid-account))) 
          (let [account (logic/new-account (:activeCard valid-account)
                                           (:availableLimit valid-account))]
            (port/save-account! account)
            (return-account))
          (when (nil? valid-account) 
            (port/add-violations! [:illegal-account-reset])
            (return-account)))))))

(defn new-transaction 
  "Glues the whole transaction flow, validates the request, maps the json, authorizes and returns an answer to the client"
  [transaction]
  (when (not (empty-body? transaction))
    (let [mapped-transaction (adapter/json->map transaction)
          valid-transaction  (validator/transaction mapped-transaction)]
      (if (boolean mapped-transaction)
        (when (boolean valid-transaction)
          (dosync
           (port/save-transaction! valid-transaction) 
           (request-transaction-auth! valid-transaction)
           (return-account)))
        nil))))
