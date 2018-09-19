(ns demonad.state-runtime
  "Run a monadic address book program"
  (:require [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]))

(defn state-run-command
  "Run a monadic address book program -- with state as simple backend storage."
  [run env state m]
  (cond
    (lang/put? m)
    (let [address (lang/put-address m)]
      [true
       (assoc state (address/address-id address) address)])

    (lang/get? m)
    [(get state (lang/get-id m))
     state]

    (lang/delete? m)
    [true
     (dissoc state (address/address-id (lang/delete-address m)))]

    (lang/filter? m)
    [(filter (lang/filter-predicate? m) (vals state))
     state]

    :else
    monad/unknown-command))

(defn state-command-config
  [initial-address-book-state]
  (monad/make-monad-command-config state-run-command
                                   {}
                                   initial-address-book-state))

(defn state-run-monad
  [m]
  (first (monad/execute-free-reader-state-exception
          (state-command-config {1 (address/make-address 1 "Marcus" "Tübingen")})
          m)))
