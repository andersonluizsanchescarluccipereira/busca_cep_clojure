(ns busca-cep.adapters.memory-cache-test
  (:require [clojure.test :refer :all]
            [busca-cep.adapters.memory-cache :as cache]
            [busca-cep.ports.cep-port :as port]))

;; Fake adapter para simular chamada externa
(defrecord FakeAdapter [calls response]
  port/CepPort
  (fetch-cep [_ cep]
    ;; registra quantas vezes foi chamado
    (swap! calls inc)
    response))

(deftest memory-cache-fetch
  (testing "memory cache should call adapter once and then use cache"
    (let [calls (atom 0)
          response {:cep "02442090" :logradouro "Rua X"}
          fake (->FakeAdapter calls response)

          sut (cache/new-cache fake)]

      ;; primeira chamada → deve chamar o adapter
      (let [res1 (port/fetch-cep sut "02442090")]
        (is (= res1 response))
        (is (= @calls 1)))

      ;; segunda chamada → deve vir do cache, sem chamar o adapter
      (let [res2 (port/fetch-cep sut "02442090")]
        (is (= res2 response))
        (is (= @calls 1)) ;; continua 1 → não chamou de novo
        ))))
