(ns busca-cep.system
  (:require
    [busca-cep.adapters.via-cep :as via]
    [busca-cep.adapters.dynamo-cache :as cache]
    [busca-cep.http.server :as server]))

(def system-state (atom nil))

(defn build-system []
  {:adapter (via/new-adapter)
   :http-server server/start!})

(defn start! []
  (let [adapter (via/new-adapter)
        cached (cache/new-cache adapter nil nil)
        srv (server/start! cached)]
    (reset! system-state
            {:adapter cached
             :http-server srv})))

(defn stop! []
  (reset! system-state nil))