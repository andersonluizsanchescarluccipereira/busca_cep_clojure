(ns busca-cep.system
  (:require
   [busca-cep.adapters.via-cep :as via]
   [busca-cep.adapters.dynamo-cache :as cache]
   [busca-cep.infra.dynamo :as dynamo]
   [busca-cep.http.server :as http-server]
   [busca-cep.adapters.grpc-server :as grpc-server]
   [busca-cep.adapters.kafka-consumer :as kafka-consumer]
   [busca-cep.adapters.soap-server :as soap-server]))

(defonce system-state (atom nil))

(defn build-system []
  (let [real-adapter   (via/new-adapter)
        dynamo-client  (dynamo/client)
        cache-adapter  (cache/new-cache real-adapter
                                        dynamo-client
                                        "cep-cache")]
    {:adapter cache-adapter
     :http-server  (fn [] (http-server/start! cache-adapter))
     :grpc-server  (fn [] (grpc-server/start-grpc-server cache-adapter 50051))
     :kafka-consumer (fn [] (kafka-consumer/start-kafka-consumer cache-adapter "cep-requests" "cep-responses"))
     :soap-server  (fn [] (soap-server/start-soap-server cache-adapter 8081))}))

(defn start! []
  (let [sys (build-system)]
    ((:http-server sys))
    ((:grpc-server sys))
    ((:kafka-consumer sys))
    ((:soap-server sys))
    (reset! system-state sys)
    (println "Sistema iniciado com múltiplas interfaces: HTTP, gRPC, Kafka, SOAP")))

(defn stop! []
  (when @system-state
    ;; Stop servers if needed
    (reset! system-state nil)
    (println "Sistema parado")))
