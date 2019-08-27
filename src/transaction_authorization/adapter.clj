(ns transaction-authorization.adapter
  (:require [clojure.data.json :as json]))

(defn json->map [json-data]
  (json/read-str json-data :key-fn keyword))

(defn map->json [map-data]
  (json/write-str map-data))
