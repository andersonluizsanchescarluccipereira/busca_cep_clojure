(ns busca-cep.adapters.via-cep-test
  (:require [clojure.test :refer :all]
            [busca-cep.adapters.via-cep :as via]
            [busca-cep.ports.cep-port :as port]))

(deftest via-cep-fetch
  (testing "via-cep calls http/get and parses correctly"
    (with-redefs [clj-http.client/get
                  (fn [_ _]
                    {:status 200
                     :body {:cep "02442090" :logradouro "Rua X"}})] ;; chave keyword no mock
      (let [adapter (via/new-adapter)
            res (port/fetch-cep adapter "02442090")]
        (is (= "02442090" (:cep res)))
        (is (= "Rua X" (:logradouro res)))))))
