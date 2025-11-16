(ns busca-cep.adapters.memory-cache
  (:require [busca-cep.ports.cep-port :as port]))

(defn new-cache [cep-adapter]
  (let [db (atom {})]
    (reify port/CepPort
      (fetch-cep [_ cep]
        (if-let [cached (get @db cep)]
          cached
          (let [res (port/fetch-cep cep-adapter cep)]
            (swap! db assoc cep res)
            res))))))
