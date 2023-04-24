(ns demonad.database-runtime
  "Run a monadic address book program"
  (:require [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]
            [clojure.java.jdbc :as jdbc]))

(defn put-address
  [db-spec address]
  (jdbc/insert! db-spec "addresses" (into {} address)))

(defn delete-address
  [db-spec id]
  (jdbc/delete! db-spec "addresses" ["id = ?" id]))

(defn get-address
  [db-spec id]
  (first (jdbc/query db-spec ["SELECT * FROM addresses where id = ?" id]
                     {:row-fn address/map->Address})))

(defn filter-addresses
  [db-spec predicate?]
  (filter predicate?
          (jdbc/query db-spec ["SELECT * FROM addresses"]
                      {:row-fn address/map->Address})))

(defn database-run-command
  "Run a monadic address book program -- with H2 database as backend storage."
  [_run env state m]
  (cond
    (lang/put-address? m)
    [(put-address (::db-spec env) (lang/put-address-address m)) state]

    (lang/delete-address? m)
    [(delete-address (::db-spec env) (lang/delete-address-id m)) state]

    (lang/get-address? m)
    [(get-address (::db-spec env) (lang/get-address-id m)) state]

    (lang/filter-addresses? m)
    [(filter-addresses (::db-spec env) (lang/filter-addresses-predicate? m)) state]

    :else
    monad/unknown-command))

(defn database-monad-command-config
  [db]
  (monad/make-monad-command-config database-run-command
                                   {::db-spec db}
                                   {}))

(defn database-run-monad
  [m]
  (let [initial-address-book-database
        [(address/make-address 1 "Marcus" "Tübingen")]
        db
        {:classname "org.h2.Driver"
         :subprotocol "h2"
         :subname (str "mem:demonad")}]
    (jdbc/with-db-connection [_conn db]
      (jdbc/db-do-commands
       db
       (jdbc/create-table-ddl :addresses
                              [[:id :int]
                               [:name :varchar]
                               [:town :varchar]]
                              {:conditional? true}))
      (jdbc/insert-multi! db :addresses
                          (map #(into {} %) initial-address-book-database))
      (first (monad/execute-monadic
               (database-monad-command-config db)
               m)))))
