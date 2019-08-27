(ns transaction-authorization.util
  (:require [clojure.string :as string])
  (:import  [java.time ZonedDateTime
                       DateTimeException]))

(defn zoned-datetime? [d]
  (try 
    (boolean (ZonedDateTime/parse d))
    (catch DateTimeException e false)))
