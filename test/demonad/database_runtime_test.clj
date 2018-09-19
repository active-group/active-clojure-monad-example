(ns demonad.database-runtime-test
  (:require [demonad.database-runtime :refer :all]
            [demonad.lang :as lang]
            [demonad.address :as address]
            [active.clojure.monad :as monad]
            [active.clojure.mock-monad :as mock]
            [clojure.test :refer :all]))

(deftest t-database-put-get
  (is (= (address/make-address 2 "Mike" "Tübingen")
         (database-run-monad
          (monad/monadic
           (lang/put (address/make-address 2 "Mike" "Tübingen"))
           (lang/get 2))))))

(deftest t-database-get
  (is (not= (address/make-address 2 "Mike" "Tübingen")
            (database-run-monad
             (monad/monadic
              (lang/get 2))))))

(deftest t-filter
  (is (= [(address/make-address 2 "Mike" "Tübingen")]
         (database-run-monad
          (monad/monadic
           (lang/put (address/make-address 2 "Mike" "Tübingen"))
           (lang/filter (fn [addr] (= 2 (address/address-id addr)))))))))

(deftest t-get-all
  (is (= 1
         (count
          (database-run-monad
           (monad/monadic
            (lang/get-all)))))))

(deftest t-remove-tübingen
  (is (= []
         (database-run-monad
          (monad/monadic
           (lang/remove-tübingen)
           (lang/filter (constantly true)))))))
