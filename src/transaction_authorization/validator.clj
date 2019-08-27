(ns transaction-authorization.validator
  (:require [transaction-authorization.util :as util]
            [transaction-authorization.port :as port]))

(defn account [account]
  (let [account-data (:account account)]
    (if (and
         (boolean? (:activeCard     account-data))
         (number?  (:availableLimit account-data))
         (not (port/has-account?)))
      account-data)))

(defn transaction [transaction]
  (let [transaction-data (:transaction transaction)]
    (if (and
         (string?              (:merchant transaction-data))
         (number?              (:amount   transaction-data))
         (util/zoned-datetime? (:time transaction-data))
         (port/has-account?))
      transaction-data)))
