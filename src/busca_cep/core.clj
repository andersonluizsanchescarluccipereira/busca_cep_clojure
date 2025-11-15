(ns busca-cep.core
  (:require [busca-cep.config.routes :as routes]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& _]
  (println "Servidor rodando em http://localhost:3000")
  ;; :join? false permite encerrar com Ctrl+C sem travar
  (run-jetty routes/app {:port 3000 :join? false}))
