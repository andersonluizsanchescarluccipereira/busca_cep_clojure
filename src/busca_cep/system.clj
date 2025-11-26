(ns busca-cep.system
  (:require
   [busca-cep.adapters.via-cep :as via]
   [busca-cep.adapters.dynamo-cache :as cache]
   [busca-cep.infra.dynamo :as dynamo]
   [busca-cep.http.server :as server]))

(defonce system-state (atom nil))

(defn build-system []
  (let [real-adapter   (via/new-adapter)
        dynamo-client  (dynamo/client)
        cache-adapter  (cache/new-cache real-adapter
                                        dynamo-client
                                        "cep-cache")]
    {:adapter cache-adapter
     :server  server/start!}))

(defn start! []
  (let [{:keys [adapter server]} (build-system)]
    (server adapter)
    (reset! system-state {:adapter adapter})
    (println "Sistema iniciado com DynamoDB (LocalStack) + ViaCEP cacheado!")))

(defn stop! []
  (when @system-state
    (reset! system-state nil)
    (println "Sistema parado")))
