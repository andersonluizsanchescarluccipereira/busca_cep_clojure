(ns busca-cep.http.router
  (:require
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [busca-cep.ports.cep-port :as port]))

(defn make-handler [adapter]
  (ring/ring-handler
   (ring/router
    [["/status"
      {:get (fn [_]
              {:status 200
               :body {:status "ok"}})}]   ;; <<< HEALTHCHECK AQUI

     ["/cep/:cep"
      {:get (fn [{:keys [path-params]}]
              {:status 200
               :body   (port/fetch-cep adapter (:cep path-params))})}]

     ["/webhook/cep"
      {:post (fn [{:keys [body-params]}]
               (let [cep (:cep body-params)
                     result (port/fetch-cep adapter cep)]
                 {:status 200
                  :body result}))}]]

    ;; Config global do Reitit
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         muuntaja/format-request-middleware]}})

   (ring/create-default-handler)))
