(ns demonad.address
  "Representation of addresses"
  (:require [active.clojure.record :refer :all]))

(define-record-type
  ^{:doc "An address"}
  Address
  (make-address id name town)
  address?
  [^{:doc "Id of address"}
   id address-id

   ^{:doc "Name of address"}
   name address-name

   ^{:doc "Town of address"}
   town address-town])
