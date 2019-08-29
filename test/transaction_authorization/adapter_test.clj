(ns transaction-authorization.adapter-test
  (:require [midje.sweet :refer :all]
            [transaction-authorization.adapter :as adapter]))

(fact "Json mapping, creating keywords"
  (adapter/json->map "{\"account\":{\"activeCard\":true, \"availableLimit\":15}}") 
  => {:account
      {:activeCard true
       :availableLimit 15}})

(fact "Map to json, keywords are properties"
  (adapter/map->json {:account {:activeCard false :availableLimit 200}})
  => "{\"account\":{\"activeCard\":false,\"availableLimit\":200}}")
