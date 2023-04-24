(ns demonad.state-runtime
  "Run a monadic address book program"
  (:require [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]))

(defn state-run-command
  "Run a monadic address book program -- with state as simple backend storage."
  [run env state m]
  (cond
    (lang/put-address? m)
    (let [address (lang/put-address-address m)]
      [true
       (assoc state (address/address-id address) address)])

    (lang/get-address? m)
    [(get state (lang/get-address-id m))
     state]

    (lang/delete-address? m)
    [true
     (dissoc state (lang/delete-address-id m))]

    (lang/filter-addresses? m)
    [(filter (lang/filter-addresses-predicate? m) (vals state))
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
  (first (monad/execute-monadic
          (state-command-config {1 (address/make-address 1 "Marcus" "Tübingen")})
          m)))
