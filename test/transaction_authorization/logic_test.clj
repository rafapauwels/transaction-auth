(ns transaction-authorization.logic-test
  (:require [midje.sweet :refer :all]
            [transaction-authorization.logic :as logic]))

(fact "Creating new account"
  (logic/new-account true 100) => {:active-card true :available-limit 100}
  (logic/new-account false 0)  => {:active-card false :available-limit 0})

(fact "New limit after a transaction"
  (logic/limit-after-transaction 1000 600) => 400
  (logic/limit-after-transaction 200 150) => 50)

(fact "Account has enough limit for a transaction"
  (logic/enough-limit? {:active-card true :available-limit 500}
                       {:merchant "Burger King"
                        :amount 2000
                        :time "2019-02-13T10:00:00.000Z"}) => false
  (logic/enough-limit? {:active-card true :available-limit 5000}
                       {:merchant "McDonalds"
                        :amount 20
                        :time "2019-02-14T10:00:00.000Z"}) => true)

(fact "The card is not blocked"
  (logic/card-blocked? {:active-card false :available-limit 100}) => true
  (logic/card-blocked? {:active-card true :available-limit 100}) => false)

(fact "Is the last transaction similar to the current transaction"
  (logic/similar-transaction? {:merchant "Burger King" :amount 30.99}
                              {:merchant "Burger King" :amount 30.99}) => true
  (logic/similar-transaction? {:merchant "Burger King" :amount 32.99}
                              {:merchant "Burger Queen" :amount 32.99}) => false
  (logic/similar-transaction? {:merchant "Burger King" :amount 50.01}
                              {:merchant "Burger KIng" :amount 50.10}) => false)

(fact "No more than 3 transaction can happen on a 2 minute interval"
  (logic/high-frequency? {:merchant "Burger King" :amount 30.99 :time "2019-02-13T10:47:01.000Z"}
                         '({:merchant "Burger" :amount 32 :time "2019-02-13T10:45:31.000Z"}
                          {:merchant "Burger K" :amount 33 :time "2019-02-13T10:46:00.000Z"})) => true
  (logic/high-frequency? {:merchant "Burger King" :amount 30.99 :time "2019-02-13T10:50:01.000Z"}
                         '({:merchant "Burger" :amount 32 :time "2019-02-13T10:45:31.000Z"}
                          {:merchant "Burger K" :amount 33 :time "2019-02-13T10:48:01.000Z"})) => false)

(fact "A doubled transaction occur when similar transactions happen on a 2 minute interval"
  (logic/doubled-transaction? {:merchant "BK" :amount 30.99 :time "2019-02-13T10:45:01.000Z"}
                              '({:merchant "BK" :amount 30.99 :time "2019-02-13T10:45:01.000Z"})) => true
  (logic/doubled-transaction? {:merchant "BK" :amount 30.99 :time "2019-02-13T10:45:01.000Z"}
                              '({:merchant "BK" :amount 30.99 :time "2019-02-13T10:42:01.000Z"})) => false)
