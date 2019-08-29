(defproject transaction-authorization "1.0.0-SNAPSHOT"
  :description "Transaction authorization service. An exercise for Nubank"
  :url ""
  :license {:name "GNU GPL 3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :plugins [[lein-midje "3.2.1"]]
  :dependencies [[org.clojure/data.json "0.2.6"]
                 [org.clojure/clojure "1.10.0"]
                 [ring/ring-defaults "0.3.2"]
                 [http-kit "2.3.0"]
                 [compojure "1.6.1"]]
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[midje "1.9.1"]
                                  [nubank/selvage "0.0.1"]]}
             :uberjar {:aot :all}}
  :main ^{:skip-aot true} transaction-authorization.server)
