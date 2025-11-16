(ns busca-cep.system
  (:require
   [busca-cep.adapters.via-cep :as via]
   [busca-cep.http.server :as server]))

(defonce system-state (atom nil))

(defn build-system []
  {:adapter (via/new-adapter)    ;; << usando somente ViaCEP
   :server  server/start!})

(defn start! []
  (let [{:keys [adapter server]} (build-system)]
    (server adapter)            ;; injeta o adapter no servidor HTTP
    (reset! system-state {:adapter adapter})
    (println "Sistema iniciado com ViaCEP")))

(defn stop! []
  (when-let [_ @system-state]
    (reset! system-state nil)
    (println "Sistema parado")))
