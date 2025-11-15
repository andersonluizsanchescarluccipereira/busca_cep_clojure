(ns busca-cep.config.routes
  (:require
   [reitit.ring :as ring]
   [muuntaja.core :as mu]
   [muuntaja.middleware :as mu.middleware]
   [ring.util.response :as response]
   [clj-http.client :as http]
   [cheshire.core :as json]))

(def m (mu/create))

(defn buscar-cep [cep]
  (let [url (str "https://viacep.com.br/ws/" cep "/json/")
        resp (http/get url {:as :json})]
    (if (= 200 (:status resp))
      (:body resp)
      {:erro "CEP inválido"})))

(def app
  (ring/ring-handler
   (ring/router
    [["/cep/:cep"
      {:get
       (fn [req]
         (let [cep (get-in req [:path-params :cep])
               dados (buscar-cep cep)]
           (response/response dados)))}]]
    {:data {:muuntaja m
            :middleware [mu.middleware/wrap-format]}})
   (ring/create-default-handler)))
