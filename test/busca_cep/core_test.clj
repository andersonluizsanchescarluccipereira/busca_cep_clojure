(ns busca-cep.core-test
  (:require [clojure.test :refer :all]
            [busca-cep.core :refer :all]
            [busca-cep.ports.cep-port :as port]))

(defrecord DummyCepPort []
  port/CepPort
  (fetch-cep [_ cep]
    {:cep cep :logradouro "Rua Teste" :uf "SP"}))

(deftest consultar-cep-test
  (let [cep-port (->DummyCepPort)
        cep "12345678"
        result (consultar-cep {:cep-port cep-port} cep)]
    (is (= {:cep cep :logradouro "Rua Teste" :uf "SP"} result))))
