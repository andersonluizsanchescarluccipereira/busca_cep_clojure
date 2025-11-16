(ns busca-cep.adapters.via-cep
  (:require
   [clj-http.client :as http]
   [cheshire.core :as json]
   [busca-cep.ports.cep-port :as port]))

(defrecord ViaCepAdapter [base-url])

(defn new-adapter []
  (->ViaCepAdapter "https://viacep.com.br/ws"))

(extend-type ViaCepAdapter
  port/CepPort
  (fetch-cep [_ cep]
    (let [url (str (:base-url _) "/" cep "/json/")
          res (http/get url {:as :json})]
      (:body res))))
