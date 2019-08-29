(ns transaction-authorization.logic
  (:require [transaction-authorization.util :as util]))

(defn new-account [active-card available-limit]
  {:active-card     active-card
   :available-limit available-limit})

(defn less-than-2-minutes? [timediff]
  (and (= 0 (:years timediff))
       (= 0 (:days timediff))
       (= 0 (:hours timediff))
       (> 2 (:minutes timediff))))

(defn similar-transaction? [current last]
  (and (= (:merchant current) (:merchant last))
       (= (:amount current)   (:amount last))))

(defn enough-limit?
  "The transaction amount should not exceed the available limit"
  [account t]
  (>= (:available-limit account) (:amount t)))

(defn doubled-transaction? 
  "There should not be more than 2 similar transactions on a 2 minute interval"
  [t ts]
  (let [current-transaction t
        last-transaction    (first ts)]
    (if (similar-transaction? current-transaction last-transaction)
      (let [current-time (:time current-transaction)
            last-time    (:time last-transaction)
            timediff     (util/datetime-diff last-time current-time)]
        (if (less-than-2-minutes? timediff) true false))
      false)))

(defn card-blocked? 
  "No transaction should be allowed if the card is blocked"
  [account]
  (not (:active-card account)))

(defn high-frequency? 
  "There should be no more than 3 transactions on a 2 minute interval"
  [t ts]
  (if (or (nil? ts) (= 1 (count ts)))
    false
    (let [current  (:time t)
          third    (:time (second ts))
          timediff (util/datetime-diff third current)]
      (if (less-than-2-minutes? timediff) true false))))

(defn get-violations [t ts account-info]
  {:card-blocked                  (card-blocked? account-info)
   :insufficient-limit            (not (enough-limit? account-info t))
   :doubled-transaction           (doubled-transaction? t ts)
   :high-frequency-small-interval (high-frequency? t ts)})

(defn authorize [account latest-transactions]
  (let [[t & ts]     latest-transactions
        account-info (:account account)
        violations   (get-violations t ts account-info)]
    (if (every? false? (map #(second %) violations))
      {:authorized true}
      {:authorized false :violations (->> violations
                                          (filter #(true? (second %)))
                                          (map first))})))

(defn limit-after-transaction [available-limit transaction-amount]
  (- available-limit transaction-amount))
