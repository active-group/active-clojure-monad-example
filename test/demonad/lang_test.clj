(ns demonad.lang-test
  (:require [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]
            [active.clojure.mock-monad :as mock]
            [clojure.test :refer :all]))

(deftest t-state-put-get
  (is (= (address/make-address 2 "Mike" "Tübingen")
         (mock/mock-run-monad
          [(mock/mock-result (lang/put-address (address/make-address 2 "Mike" "Tübingen")) true)
           (mock/mock-result (lang/get-address 2) (address/make-address 2 "Mike" "Tübingen"))]
          (monad/monadic
           (lang/put-address (address/make-address 2 "Mike" "Tübingen"))
           (lang/get-address 2))))))

(deftest t-state-get
  (is (not= (address/make-address 2 "Mike" "Tübingen")
            (mock/mock-run-monad
             [(mock/mock-result (lang/get-address 2) nil)]
             (monad/monadic
              (lang/get-address 2))))))

(deftest t-filter
  (is (= [(address/make-address 2 "Mike" "Tübingen")]
         (mock/mock-run-monad
          [(mock/mock-result (lang/put-address (address/make-address 2 "Mike" "Tübingen")) true)
           (mock/mock (fn [m]
                        (lang/filter-addresses? m))
                      (constantly (monad/return [(address/make-address 2 "Mike" "Tübingen")])))]
          (monad/monadic
           (lang/put-address (address/make-address 2 "Mike" "Tübingen"))
           (lang/filter-addresses (fn [addr] (= 2 (address/address-id addr)))))))))

(deftest t-get-all-addresses
  (is (= 1
         (count
          (mock/mock-run-monad
           [(mock/mock-result (lang/get-all-addresses) ['fake-address])]
           (monad/monadic
            (lang/get-all-addresses)))))))

(deftest t-remove-addresses-in-tübingen
  (is (nil?
       (mock/mock-run-monad
        [(mock/mock lang/filter-addresses?
                    (constantly (monad/return [(address/make-address 2 "Mike" "Tübingen")])))
         (mock/mock-result (lang/delete-address 2) true)
         (mock/mock-result (lang/get-address 2) nil)]
        (monad/monadic
         (lang/remove-addresses-in-tübingen)
         (lang/get-address 2))))))
