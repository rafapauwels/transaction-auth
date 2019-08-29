(ns transaction-authorization.transaction-flow
  (:require
   [midje.sweet :refer :all]
   [selvage.flow :refer [*world* flow]]
   [transaction-authorization.server :as server]
   [transaction-authorization.port :as port]
   [transaction-authorization.db :as db]))

(defn init! [world]
  (assoc world :system {}))

(flow "Create transactions based on an existing account"
  init!

  ;; Setup a mock account
  (fn [world]
    (assoc world :account
           (server/new-account-endpoint {:body
                                         (.getBytes "{\"account\":{\"activeCard\":true, \"availableLimit\": 600}}")})))

  (fact "A mock account exists"
    (port/has-account?) => true
    (port/account-state) => {:account {:activeCard true :availableLimit 600}})

  ;; Creates new transaction
  (fn [world]
    (assoc world
           :transaction {:body
                         (.getBytes "{\"transaction\":{\"merchant\":\"BK\",\"amount\":120,\"time\":\"2019-02-13T10:45:00.000Z\"}}")}))

  ;; Send first transaction
  (fn [world]
    (assoc world :answer
           (server/new-transaction-endpoint (:transaction world))))

  (fn [world]
    (fact "The transaction should be authorized"
      (port/account-state) => {:account {:activeCard true :availableLimit 480}}
      (:body (:answer world)) => "{\"account\":{\"activeCard\":true,\"availableLimit\":480},\"violations\":[]}")
    world)


  ;; Repeats the same transaction
  (fn [world]
    (assoc world :answer
           (server/new-transaction-endpoint (:transaction world))))

  (fn [world]
    (fact "It was a doubled transaction"
      (port/account-state) => {:account {:activeCard true :availableLimit 480}}
      (:body (:answer world)) => "{\"account\":{\"activeCard\":true,\"availableLimit\":480},\"violations\":[\"doubled-transaction\"]}")
    world)

  ;; Sets new transaction
  (fn [world]
    (assoc world
           :transaction {:body
                         (.getBytes "{\"transaction\":{\"merchant\":\"BK\",\"amount\":30,\"time\":\"2019-02-13T10:46:00.000Z\"}}")}))

  ;; Sends a new transaction within 2 minutes
  (fn [world]
    (assoc world :answer
           (server/new-transaction-endpoint (:transaction world))))

  (fn [world]
    (fact "High frequency transaction"
      (port/account-state) => {:account {:activeCard true :availableLimit 480}}
      (:body (:answer world)) => "{\"account\":{\"activeCard\":true,\"availableLimit\":480},\"violations\":[\"high-frequency-small-interval\"]}")
    world)

  ;; Sets new transaction
  (fn [world]
    (assoc world
           :transaction {:body
                         (.getBytes "{\"transaction\":{\"merchant\":\"BBQ\",\"amount\":300,\"time\":\"2019-02-13T11:40:40.000Z\"}}")}))

  (fn [world]
    (assoc world :answer
           (server/new-transaction-endpoint (:transaction world))))

  (fn [world]
    (fact "Valid transaction, should be accepted"
      (port/account-state) => {:account {:activeCard true :availableLimit 180}}
      (:body (:answer world)) => "{\"account\":{\"activeCard\":true,\"availableLimit\":180},\"violations\":[]}")
    world)

  ;; Sets new transaction
  (fn [world]
    (assoc world
           :transaction {:body
                         (.getBytes "{\"transaction\":{\"merchant\":\"BK\",\"amount\":200,\"time\":\"2019-02-13T11:47:00.000Z\"}}")}))

  (fn [world]
    (assoc world :answer
           (server/new-transaction-endpoint (:transaction world))))

  (fn [world]
    (fact "Not enough limit"
      (port/account-state) => {:account {:activeCard true :availableLimit 180}}
      (:body (:answer world)) => "{\"account\":{\"activeCard\":true,\"availableLimit\":180},\"violations\":[\"insufficient-limit\"]}")
    world)

  ;; Destroy mocked account
  (fn [world]
    (db/delete! :mock-account)
    {}))
