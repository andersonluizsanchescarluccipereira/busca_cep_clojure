(ns busca-cep.adapters.via-cep
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [busca-cep.ports.cep-port :as port]))

(defn fetch-remote [cep]
  (let [url (format "https://viacep.com.br/ws/%s/json" cep)
        res (http/get url {:as :json-string-keys})]
    (:body res)))

(defn new-adapter []
  (reify port/CepPort
    (fetch-cep [_ cep]
      (fetch-remote cep))))
