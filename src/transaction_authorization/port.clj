(ns transaction-authorization.port
  (:require [transaction-authorization.db :as db]))

(defn edit-account! [keyword acc] (db/patch! keyword acc))

(defn save-account! [acc] (db/post! :account acc))

(defn save-transaction! [tran] (db/post! :transaction tran))

(defn transaction-history [] (db/get! :transaction))

(defn has-account? [] (boolean (db/get! :account)))

(defn current-violation [] {:violations (db/get! :current-violation)})

(defn add-violations! [violations] (db/post! :current-violation violations))

(defn stash-current-violation! []
  (dosync
   (db/post! :violation (:violations (db/get! :current-violation)))
   (db/delete! :current-violation)))

(defn account-state 
  ([] 
   (account-state "camel"))
  ([notation]
   (let [account (db/get! :account)]
     (when (not (nil? account))
       (if (= notation "camel")
         {:account {:activeCard     (:active-card account)
                    :availableLimit (:available-limit account)}}
         {:account {:active-card     (:active-card account)
                    :available-limit (:available-limit account)}})))))
