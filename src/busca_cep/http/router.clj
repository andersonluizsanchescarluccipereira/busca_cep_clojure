(ns busca-cep.http.router
  (:require
    [reitit.ring :as ring]
    [muuntaja.core :as m]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [busca-cep.ports.cep-port :as port]
    [clojure.java.io :as io]))

(defn make-handler [adapter]
  (ring/ring-handler
   (ring/router
    [["/status"
      {:get (fn [_]
              {:status 200
               :body {:status "ok"}})}]

     ["/cep/:cep"
      {:get (fn [{:keys [path-params]}]
              {:status 200
               :body (port/fetch-cep adapter (:cep path-params))})}]

     ["/webhook/cep"
      {:post (fn [{:keys [body-params]}]
               (let [cep (:cep body-params)
                     result (port/fetch-cep adapter cep)]
                 {:status 200
                  :body result}))}]

     ;; 🔥 SOAP endpoint (AGORA EXISTE)
     ["/soap"
      {:post (fn [request]
               (let [xml (slurp (:body request))
                     _ (println "SOAP REQUEST:" xml) ;; debug útil
                     cep (second (re-find #"<cep:cep>(.*?)</cep:cep>" xml))
                     result (port/fetch-cep adapter cep)]
                 {:status 200
                  :headers {"Content-Type" "text/xml; charset=utf-8"}
                  :body (str
                         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                         "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                         "<soap:Body>"
                         "<GetCepResponse xmlns=\"http://example.com/cep\">"
                         "<cepInfo>" result "</cepInfo>"
                         "</GetCepResponse>"
                         "</soap:Body>"
                         "</soap:Envelope>")}))}]

     ;; 📄 WSDL
     ["/service"
      {:get (fn [req]
              (let [qs (:query-string req)]
                (if (and qs (re-find #"(^|&)wsdl(=|&|$)" qs))
                  (let [res (io/resource "wsdl/service.wsdl")]
                    (if res
                      {:status 200
                       :headers {"Content-Type" "text/xml; charset=utf-8"}
                       :body (slurp res)}
                      {:status 404 :body "WSDL not found"}))
                  {:status 404 :body "Not Found"})))}]]

    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         muuntaja/format-request-middleware]}})

   (ring/create-default-handler)))