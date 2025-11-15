(ns busca-cep.component.via-cep-client
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defn buscar-cep [cep]
  (let [url (str "https://viacep.com.br/ws/" cep "/json/")
        resp (http/get url {:throw-exceptions false})]
    (when (:body resp)
      (json/parse-string (:body resp) true))))