(ns busca-cep.adapters.via-cep-test
  (:require [clojure.test :refer :all]
            [clj-http.client :as http]
            [busca-cep.adapters.via-cep :as via]))

(deftest fetch-cep-stub
  (testing "fetch-cep uses HTTP client"
    (with-redefs [http/get (fn [_ _]
                             {:status 200
                              :body "{\"cep\":\"02442090\",\"logradouro\":\"Rua X\"}"})]
      (let [res (via/fetch-cep "02442090")]
        (is (= "02442090" (:cep res)))
        (is (= "Rua X" (:logradouro res)))))))
