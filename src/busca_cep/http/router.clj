(ns busca-cep.http.router
  (:require
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [busca-cep.ports.cep-port :as port]))

(defn make-handler [adapter]
  (ring/ring-handler
   (ring/router
    [["/cep/:cep"
      {:get (fn [{:keys [path-params]}]
              {:status 200
               :body   (port/fetch-cep adapter (:cep path-params))})}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         muuntaja/format-request-middleware]}})
   (ring/create-default-handler)))
