(ns busca-cep.adapters.dynamo-cache-test
  (:require [clojure.test :refer :all]
            [busca-cep.adapters.dynamo-cache :as cache]
            [busca-cep.ports.cep-port :as port]
            [cheshire.core :as json])
  (:import (software.amazon.awssdk.services.dynamodb.model
            GetItemResponse
            PutItemResponse
            AttributeValue)
           (software.amazon.awssdk.services.dynamodb DynamoDbClient)))

;; Mock Dynamo Client
(defn mock-dynamo-client [store]
  (proxy [DynamoDbClient] []
    (getItem [req]
      (let [cep (-> req .key (get "cep") .s)
            item (get @store cep)]
        (-> (GetItemResponse/builder)
            (.item (or item {}))
            .build)))
    (putItem [req]
      (let [item (.item req)
            cep (-> item (get "cep") .s)
            data (-> item (get "data") .s)
            ttl (-> item (get "ttl") .n)]
        (swap! store assoc cep {"data" (AttributeValue/fromS data)
                                "ttl" (AttributeValue/fromN ttl)})
        (-> (PutItemResponse/builder) .build)))
    (close [])))

(deftest dynamo-cache-test
  (let [store (atom {})
        mock-dynamo (mock-dynamo-client store)
        real-adapter (reify port/CepPort
                       (fetch-cep [_ cep] {:cep cep :logradouro "Rua Teste"}))
        sut (cache/new-cache real-adapter mock-dynamo "test-table")]

    ;; First call - should fetch from adapter and cache
    (let [result (port/fetch-cep sut "12345678")]
      (is (= {:cep "12345678" :logradouro "Rua Teste"} result))
      (is (contains? @store "12345678")))

    ;; Second call - should hit cache
    (let [result (port/fetch-cep sut "12345678")]
      (is (= {:cep "12345678" :logradouro "Rua Teste"} result)))

    ;; Simulate expired TTL
    (let [expired-ttl (str (- (quot (System/currentTimeMillis) 1000) 100))] ; past
      (swap! store assoc "12345678" {"data" (AttributeValue/fromS (json/generate-string {:cep "12345678" :logradouro "Rua Teste"}))
                                     "ttl" (AttributeValue/fromN expired-ttl)}))
    ;; Third call - should refetch because TTL expired
    (let [result (port/fetch-cep sut "12345678")]
      (is (= {:cep "12345678" :logradouro "Rua Teste"} result)))))
