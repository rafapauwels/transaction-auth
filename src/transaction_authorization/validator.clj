(ns transaction-authorization.validator
  (:require [transaction-authorization.util :as util]
            [transaction-authorization.port :as port]))

(defn account 
  "Defines the account archetype"
  [account]
  (let [account-data   (:account account)
        account-exists (port/has-account?)]
    (if (and
         (boolean? (:activeCard     account-data))
         (number?  (:availableLimit account-data))
         (not account-exists))
      account-data
      (when (not account-exists)
        :bad-format))))

(defn transaction 
  "Defines the transaction archetype"
  [transaction]
  (let [transaction-data (:transaction transaction)]
    (if (and
         (string?              (:merchant transaction-data))
         (number?              (:amount   transaction-data))
         (util/zoned-datetime? (:time transaction-data))
         (port/has-account?))
      transaction-data)))
