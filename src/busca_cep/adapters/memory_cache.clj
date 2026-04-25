(ns busca-cep.adapters.memory-cache
  (:require [busca-cep.ports.cep-port :as port]))

(defn new-cache [real-adapter]
  (let [cache (atom {})]
    (reify port/CepPort
      (fetch-cep [_ cep]
        (if-let [cached (get @cache cep)]
          cached
          (let [result (port/fetch-cep real-adapter cep)]
            (swap! cache assoc cep result)
            result))))))
