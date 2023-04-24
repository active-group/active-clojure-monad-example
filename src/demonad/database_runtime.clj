(ns demonad.database-runtime
  "Run a monadic address book program"
  (:require [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]
            [clojure.java.jdbc :as jdbc]))

(defn database-run-command
  "Run a monadic address book program -- with H2 database as backend storage."
  [run env state m]
  (cond
    (lang/put? m)
    (let [address (lang/put-address m)]
      [(jdbc/insert! env :addresses (into {} address))
       state])

    (lang/get? m)

    [(first (jdbc/query env ["SELECT * FROM addresses where id = ?" (lang/get-id m)]
                        {:row-fn address/map->Address}))
     state]

    (lang/delete? m)
    [(jdbc/delete! env :addresses ["id = ?" (address/address-id (lang/delete-address m))])
     state]

    (lang/filter? m)
    [(filter (lang/filter-predicate? m)
             (jdbc/query env ["SELECT * FROM addresses"]
                         {:row-fn address/map->Address}))
     state]

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
