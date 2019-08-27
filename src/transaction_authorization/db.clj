(ns transaction-authorization.db)

(def db-account           (ref nil))
(def db-transaction       (ref nil))
(def db-violation         (ref nil))
(def db-current-violation (ref []))

(defn patch! [k new-value]
  (dosync
   (alter db-account assoc k new-value)))

(defn post! [target value]
  (cond
    (= target :account)           (dosync (ref-set db-account value))
    (= target :transaction)       (dosync (alter db-transaction conj value))
    (= target :violation)         (dosync (alter db-violation conj value))
    (= target :current-violation) (dosync (ref-set db-current-violation value))))

(defn delete! [target]
  (cond
    (= target :current-violation) (dosync (ref-set db-current-violation []))))

(defn get! [target]
  (cond
    (= target :account)           (deref db-account)
    (= target :transaction)       (deref db-transaction)
    (= target :violation)         (deref db-violation)
    (= target :current-violation) (deref db-current-violation)))
