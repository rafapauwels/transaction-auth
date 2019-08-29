(ns transaction-authorization.adapter
  (:require [clojure.data.json :as json]))

(defn json->map [json-data]
  (try
    (json/read-str json-data :key-fn keyword)
    (catch Exception e nil)))

(defn map->json [map-data]
  (try
    (json/write-str map-data)
    (catch Exception e nil)))
