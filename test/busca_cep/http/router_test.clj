(ns busca-cep.http.router-test
  (:require
   [clojure.test :refer :all]
   [busca-cep.http.router :as router]
   [ring.mock.request :as mock]))

(deftest status-route-test
  (let [handler (router/make-handler nil) ;; adapter não usado no /status
        request (mock/request :get "/status")
        response (handler request)]
    (is (= 200 (:status response)))
    (is (= {:status "ok"} (:body response)))))

(deftest cep-route-test
  (let [fake-adapter
        (reify
          ;; Mock do protocolo CepPort
          busca-cep.ports.cep-port/CepPort
          (fetch-cep [_ _cep]
            {:cep "12345678" :logradouro "Rua Fake" :uf "SP"}))

        handler (router/make-handler fake-adapter)
        request (mock/request :get "/cep/12345678")
        response (handler request)]

    (is (= 200 (:status response)))
    (is (= {:cep "12345678" :logradouro "Rua Fake" :uf "SP"} (:body response)))))
