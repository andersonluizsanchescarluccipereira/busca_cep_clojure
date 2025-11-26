(ns busca-cep.adapters.dynamo-cache
  (:require [busca-cep.ports.cep-port :as port]
            [cheshire.core :as json])
  (:import
   (software.amazon.awssdk.services.dynamodb DynamoDbClient)
   (software.amazon.awssdk.services.dynamodb.model
    GetItemRequest
    PutItemRequest
    AttributeValue)))

(def ttl-segundos (* 5 60)) ;; 5 minutos

(defn agora+ttl []
  (+ (quot (System/currentTimeMillis) 1000) ttl-segundos))

(defn new-cache [real-adapter dynamo-client table]
  (reify port/CepPort
    (fetch-cep [_ cep]
      (let [req (-> (GetItemRequest/builder)
                    (.tableName table)
                    (.key {"cep" (-> (AttributeValue/builder)
                                     (.s cep)
                                     .build)})
                    .build)
            resp (.getItem dynamo-client req)
            item (.item resp)]

        (if (and item (not (.isEmpty item)))
          (let [expires   (some-> (.get item "ttl") (.n) Long/parseLong)
                agora     (quot (System/currentTimeMillis) 1000)]

            ;; ------- TTL válido? Cache HIT -------
            (if (and expires (< agora expires))
              (json/parse-string (.s (.get item "data")) true)

              ;; ------- TTL expirado → buscar ViaCEP e sobrescrever -------
              (let [result (port/fetch-cep real-adapter cep)
                    body-json (json/generate-string result)
                    put-req (-> (PutItemRequest/builder)
                                (.tableName table)
                                (.item {"cep"  (-> (AttributeValue/builder) (.s cep) .build)
                                        "data" (-> (AttributeValue/builder) (.s body-json) .build)
                                        "ttl"  (-> (AttributeValue/builder)
                                                   (.n (str (agora+ttl))) .build)})
                                .build)]
                (.putItem dynamo-client put-req)
                result)))
          (let [result (port/fetch-cep real-adapter cep)
                body-json (json/generate-string result)
                put-req (-> (PutItemRequest/builder)
                            (.tableName table)
                            (.item {"cep"  (-> (AttributeValue/builder) (.s cep) .build)
                                    "data" (-> (AttributeValue/builder) (.s body-json) .build)
                                    "ttl"  (-> (AttributeValue/builder)
                                               (.n (str (agora+ttl))) .build)})
                            .build)]
            (.putItem dynamo-client put-req)
            result))))))
