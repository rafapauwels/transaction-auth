(ns transaction-authorization.util
  (:require [clojure.string :as string])
  (:import  [java.time ZonedDateTime
                       DateTimeException]))

(defn zoned-datetime? [d]
  (try 
    (boolean (ZonedDateTime/parse d))
    (catch DateTimeException e false)))

(defn datetime-diff 
  "Subtracts dd from d, being both ZonedDateTime"
  [dd d]
  (let [date (ZonedDateTime/parse d)
        second-date (ZonedDateTime/parse dd)]
    {:years   (- (.getYear date) (.getYear second-date))
     :days    (- (.getDayOfYear date) (.getDayOfYear second-date))
     :hours   (- (.getHour date) (.getHour second-date))
     :minutes (- (.getMinute date) (.getMinute second-date))
     :seconds (- (.getSecond date) (.getSecond second-date))
     :nano    (- (.getNano date) (.getNano second-date))}))
