(ns demonad.lang-test
  (:require [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]
            [active.clojure.mock-monad :as mock]
            [clojure.test :refer :all]))

(deftest t-state-put-get
  (is (= (address/make-address 2 "Mike" "Tübingen")
         (mock/mock-run-monad
          [(mock/mock-result (lang/put (address/make-address 2 "Mike" "Tübingen")) true)
           (mock/mock-result (lang/get 2) (address/make-address 2 "Mike" "Tübingen"))]
          (monad/monadic
           (lang/put (address/make-address 2 "Mike" "Tübingen"))
           (lang/get 2))))))

(deftest t-state-get
  (is (not= (address/make-address 2 "Mike" "Tübingen")
            (mock/mock-run-monad
             [(mock/mock-result (lang/get 2) nil)]
             (monad/monadic
              (lang/get 2))))))

(deftest t-filter
  (is (= [(address/make-address 2 "Mike" "Tübingen")]
         (mock/mock-run-monad
          [(mock/mock-result (lang/put (address/make-address 2 "Mike" "Tübingen")) true)
           (mock/mock (fn [m]
                        (lang/filter? m))
                      (constantly (monad/return [(address/make-address 2 "Mike" "Tübingen")])))]
          (monad/monadic
           (lang/put (address/make-address 2 "Mike" "Tübingen"))
           (lang/filter (fn [addr] (= 2 (address/address-id addr)))))))))

(deftest t-get-all
  (is (= 1
         (count
          (mock/mock-run-monad
           [(mock/mock-result (lang/get-all) ['fake-address])]
           (monad/monadic
            (lang/get-all)))))))

