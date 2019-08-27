(ns transaction-authorization.server
  (:gen-class)
  (:require [transaction-authorization.controller :as controller]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [clojure.edn :as edn]))

(defn new-account-endpoint [request]
  (let [body (slurp (:body request))]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (controller/new-account body)}))

(defn new-transaction-endpoint [request]
  (let [body (slurp (:body request))]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (controller/new-transaction body)}))

(defroutes entry-routes
  (POST "/accounts" [] new-account-endpoint)
  (POST "/transactions" [] new-transaction-endpoint))

(defn start-and-run! [port]
  (time (server/run-server #'entry-routes {:port port})))

(defn -main
  "'lein run' entry-point"
  [& args]
  (let [port (edn/read-string (or (System/getenv "PORT") "9443"))]
    (println (str "Starting at " port "..."))
    (start-and-run! port)))
