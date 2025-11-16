(ns busca-cep.domain.cep-test
  (:require [clojure.test :refer :all]
            [busca-cep.domain.cep :as cep]))

;; -----------------------------
;; normalize-cep
;; -----------------------------

(deftest normalize-cep-test
  (testing "normaliza CEP removendo máscara"
    (is (= "12345678" (cep/normalize-cep "12345-678")))
    (is (= "87654321" (cep/normalize-cep "87654321")))
    (is (= "02442090" (cep/normalize-cep "02442-090")))
    (is (= "02442090" (cep/normalize-cep "02442.090")))
    (is (= "02442090" (cep/normalize-cep "02442 090"))))

  (testing "retorna nil para valores inválidos"
    (is (nil? (cep/normalize-cep "1234-567")))   ;; 7 dígitos
    (is (nil? (cep/normalize-cep "ABCDE-FFF")))
    (is (nil? (cep/normalize-cep "123456789"))) ;; 9 dígitos
    (is (nil? (cep/normalize-cep "")))
    (is (nil? (cep/normalize-cep nil)))
    (is (nil? (cep/normalize-cep 12345678)))     ;; não é string
    ))

;; -----------------------------
;; valid-cep?
;; -----------------------------

(deftest valid-cep?-test
  (testing "valida formatos aceitos"
    (is (true? (cep/valid-cep? "12345678")))
    (is (true? (cep/valid-cep? "12345-678"))))

  (testing "recusa formatos inválidos"
    (is (false? (cep/valid-cep? "1234-567")))
    (is (false? (cep/valid-cep? "1234567")))
    (is (false? (cep/valid-cep? "123456789")))
    (is (false? (cep/valid-cep? "123AB678")))
    (is (false? (cep/valid-cep? nil)))
    (is (false? (cep/valid-cep? 12345678)))
    (is (false? (cep/valid-cep? "")))))

;; -----------------------------
;; format-cep
;; -----------------------------

(deftest format-cep-test
  (testing "formata corretamente 8 dígitos"
    (is (= "12345-678" (cep/format-cep "12345678")))
    (is (= "02442-090" (cep/format-cep "02442090"))))

  (testing "retorna nil para inválidos"
    (is (nil? (cep/format-cep nil)))
    (is (nil? (cep/format-cep "")))
    (is (nil? (cep/format-cep "1234567")))   ;; 7 dígitos
    (is (nil? (cep/format-cep "123456789"))) ;; 9 dígitos
    (is (nil? (cep/format-cep 12345678)))    ;; não é string
    (is (nil? (cep/format-cep "ABCDEFGH"))))) ;; letras, inválido
