(ns busca-cep.system
  (:require
   [busca-cep.adapters.via-cep :as via]
   [busca-cep.adapters.dynamo-cache :as cache]
   [busca-cep.infra.dynamo :as dynamo]
   [busca-cep.http.server :as http-server]))

(defonce system-state (atom nil))

(defn build-system []
  (let [real-adapter   (via/new-adapter)
        dynamo-client  (dynamo/client)
        cache-adapter  (cache/new-cache real-adapter
                                        dynamo-client
                                        "cep")]
    {:adapter cache-adapter
     :http-server  (fn [] (http-server/start! cache-adapter))}))

(defn start! []
  (let [sys (build-system)]
    ((:http-server sys))
    (reset! system-state sys)
    (println "Sistema iniciado com HTTP + WebSocket + SSE via Aleph")))

(defn stop! []
  (when @system-state
    (reset! system-state nil)
    (println "Sistema parado")))
