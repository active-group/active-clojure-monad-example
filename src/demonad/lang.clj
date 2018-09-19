(ns demonad.lang
  "Monadic language for adress books"
  (:require [active.clojure.record :refer :all]
            [active.clojure.monad :as monad]
            [demonad.address :as address]))

(define-record-type
  ^{:doc "Put an address into the Address book"}
  Put
  (put address)
  put?
  [^{:doc "Address to put into the Address book"}
   address put-address])

(define-record-type
  ^{:doc "Get an address from the address book"}
  Get
  (get id)
  get?
  [^{:doc "Id of the address to get from the address book"}
   id get-id])

(define-record-type
  ^{:doc "Delete an address from the address book"}
  Delete
  (delete address)
  delete?
  [^{:doc "Adress to delete from the address book"}
   address delete-address])

(define-record-type
  ^{:doc "Filter the address book"}
  Filter
  (filter predicate?)
  filter?
  [^{:doc "Predicate to filter the address book"}
   predicate? filter-predicate?])

(defn get-all
  "Return a list of all addresses in the address book"
  []
  (->
   (filter (constantly true))
   (monad/reify-as (list 'get-all))))

(defn remove-tübingen
  "Remove all addresses that are located in Tübingen."
  []
  (monad/monadic
   [tübinger (filter (fn [addr] (= "Tübingen" (address/address-town addr))))]
   (monad/sequ_ (map delete tübinger))))
