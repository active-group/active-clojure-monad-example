(ns demonad.lang
  "Monadic language for adress books"
  (:require [active.clojure.record :refer :all]
            [active.clojure.monad :as monad]
            [demonad.address :as address]))

(define-record-type
  ^{:doc "Put an address into the address book"}
  PutAddress
  (put-address address)
  put-address?
  [^{:doc "Address to put into the address book"}
   address put-address-address])

(define-record-type
  ^{:doc "Get an address from the address book"}
  GetAddress
  (get-address id)
  get-address?
  [^{:doc "ID of the address to get from the address book"}
   id get-address-id])

(define-record-type
  ^{:doc "Delete an address from the address book"}
  DeleteAddress
  (delete-address id)
  delete-address?
  [^{:doc "ID of the Address to delete from the address book"}
   id delete-address-id])

(define-record-type
  ^{:doc "Filter the address book"}
  FilterAddresses
  (filter-addresses predicate?)
  filter-addresses?
  [^{:doc "Predicate to filter the address book"}
   predicate? filter-addresses-predicate?])

(defn get-all-addresses
  "Return a list of all addresses in the address book"
  []
  (->
   (filter-addresses (constantly true))
   (monad/reify-as (list 'get-all-addresses))))

(defn in-tübingen?
  "Is a given address in Tübingen?"
  [address]
  (= "Tübingen" (address/address-town address)))

(defn remove-addresses-in-tübingen
  "Remove all addresses that are located in Tübingen."
  []
  (monad/monadic
    [tübinger (filter-addresses in-tübingen?)]
    (monad/sequ_ (map (fn [address] (delete-address (address/address-id address))) tübinger))))
