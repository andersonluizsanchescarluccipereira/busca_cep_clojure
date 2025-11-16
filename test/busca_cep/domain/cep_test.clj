(ns busca-cep.domain.cep-test
  (:require [clojure.test :refer :all]
            [busca-cep.domain.cep :as cep]))

(deftest normalize-tests
  (testing "normalize-cep"
    (is (= "02442090" (cep/normalize-cep "02442-090")))
    (is (= "02442090" (cep/normalize-cep "02442090")))
    (is (nil? (cep/normalize-cep "02442")))))

(deftest valid-cep-tests
  (testing "valid-cep?"
    (is (true? (cep/valid-cep? "02442-090")))
    (is (true? (cep/valid-cep? "02442090")))
    (is (false? (cep/valid-cep? "123")))))
