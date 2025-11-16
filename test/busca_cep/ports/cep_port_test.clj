(ns busca-cep.ports.cep-port-test
  (:require [clojure.test :refer :all]
            [busca-cep.ports.cep-port :as port]))

;; Implementação dummy para teste
(defrecord DummyCepPort []
  port/CepPort
  (fetch-cep [_ cep]
    {:cep cep
     :logradouro "Rua Teste"
     :uf "SP"}))

(deftest fetch-cep-protocol-test
  (testing "fetch-cep via DummyCepPort implementation"
    (let [impl (->DummyCepPort)
          cep "12345678"
          result (port/fetch-cep impl cep)]
      (is (= cep (:cep result)))
      (is (= "Rua Teste" (:logradouro result)))
      (is (= "SP" (:uf result))))))
