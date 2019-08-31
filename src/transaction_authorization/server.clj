(ns transaction-authorization.server
  (:gen-class)
  (:require [transaction-authorization.controller :as controller]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [clojure.edn :as edn])
  (:import [java.net InetAddress]))
          
(defn new-account-endpoint [request]
  (if (not (nil? (:body request)))
    (let [req-body (slurp (:body request))
          body (controller/new-account req-body)]
      {:status (if (nil? body) 400 200)
       :headers {"Content-Type" "text/html"}
       :body body})
    {:status 400}))

(defn new-transaction-endpoint [request]
  (if (not (nil? (:body request)))
    (let [req-body (slurp (:body request))
          body (controller/new-transaction req-body)]
      {:status (if (nil? body) 400 200)
       :headers {"Content-Type" "text/html"}
       :body body})
    {:status 400}))

(defroutes entry-routes
  (POST "/accounts" [] new-account-endpoint)
  (POST "/transactions" [] new-transaction-endpoint))

(defn start-and-run! [port]
  (time (server/run-server #'entry-routes {:port port})))

(defn -main
  "'lein run' entry-point"
  [& args]
  (let [port (edn/read-string (or (System/getenv "APP_PORT") "9443"))]
    (println (str "Starting at " (.getHostAddress (InetAddress/getLocalHost)) ":" port))
    (start-and-run! port)))
