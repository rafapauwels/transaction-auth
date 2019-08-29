(ns transaction-authorization.account-flow
  (:require
   [midje.sweet :refer :all]
   [selvage.flow :refer [*world* flow]]
   [transaction-authorization.server :as server]
   [transaction-authorization.controller :as controller]
   [transaction-authorization.port :as port]))

(defn init! [world]
  (assoc world :system {}))

(flow "Create account and store it in the interal in-memory db"
  init!

  ;; Create new account request in the world
  (fn [world]
    (assoc world 
           :request
           {:body
            (.getBytes "{\"account\":{\"activeCard\":true, \"availableLimit\": 500}}")}))
  
  (fact "There shouldn't be any account stored"
    (port/account-state) => nil
    (port/has-account?) => false)

  (fn [world]
    (assoc world :answer
     (server/new-account-endpoint (:request world))))
  
  (fn [world]
    (fact "The account should be stored in-memory without any violations"
      (port/has-account?) => true
      (port/account-state) => {:account {:activeCard true :availableLimit 500}}
      (:body (:answer world)) => "{\"account\":{\"activeCard\":true,\"availableLimit\":500},\"violations\":[]}")
    world)

  ;; Changes the account request in the world
  (fn [world]
    (assoc world 
           :request
           {:body
            (.getBytes "{\"account\":{\"activeCard\":true, \"availableLimit\": 1500}}")})) 
  
  (fn [world]
    (assoc world :answer
      (server/new-account-endpoint (:request world))))

  (fn [world]
    (fact "The account status should be the same"
      (port/has-account?) => true
      (port/account-state) => {:account {:activeCard true :availableLimit 500}}
      (:body (:answer world)) => "{\"account\":{\"activeCard\":true,\"availableLimit\":500},\"violations\":[\"illegal-account-reset\"]}")
    world)

  (fact "There should be no violation in memory, every violation was returned and stashed"
    (port/current-violation) => {:violations []}))
