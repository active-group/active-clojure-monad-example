(ns demonad.state-runtime-test
  (:require [demonad.state-runtime :refer :all]
            [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]
            [clojure.test :refer :all]))

(deftest t-state-put-get
  (is (= (address/make-address 2 "Mike" "Tübingen")
         (state-run-monad
          (monad/monadic
           (lang/put-address (address/make-address 2 "Mike" "Tübingen"))
           (lang/get-address 2))))))

(deftest t-state-get
  (is (not= (address/make-address 2 "Mike" "Tübingen")
            (state-run-monad
             (monad/monadic
              (lang/get-address 2))))))

(deftest t-filter
  (is (= [(address/make-address 2 "Mike" "Tübingen")]
         (state-run-monad
          (monad/monadic
           (lang/put-address (address/make-address 2 "Mike" "Tübingen"))
           (lang/filter-addresses (fn [addr] (= 2 (address/address-id addr)))))))))

(deftest t-get-all
  (is (= 1
         (count
          (state-run-monad
           (monad/monadic
            (lang/get-all-addresses)))))))

(deftest t-remove-tübingen
  (is (= []
         (state-run-monad
          (monad/monadic
           (lang/remove-addresses-in-tübingen)
           (lang/get-all-addresses))))))
