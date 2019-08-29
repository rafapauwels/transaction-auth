(ns transaction-authorization.controller-test
  (:require [midje.sweet :refer :all]
            [transaction-authorization.controller :as controller]))

(fact "An empty request was received"
  (controller/empty-body? nil) => true
  (controller/empty-body? {}) => true
  (controller/empty-body? ()) => true
  (controller/empty-body? {:activeCard false}) => false)
