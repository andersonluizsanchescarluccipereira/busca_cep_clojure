(ns busca-cep.adapters.soap-server
  (:require [busca-cep.ports.cep-port :as port])
  (:import [javax.jws WebService WebMethod]
           [org.apache.cxf.jaxws JaxWsServerFactoryBean]))

(gen-interface
  :name busca_cep.adapters.soap_server.CepService
  :methods [[fetchCep [String] java.util.Map]])

(deftype CepServiceImpl [adapter]
  busca_cep.adapters.soap_server.CepService
  (fetchCep [_ cep]
    (port/fetch-cep adapter cep)))

(defn start-soap-server [adapter port]
  (let [service (CepServiceImpl. adapter)
        factory (JaxWsServerFactoryBean.)]
    (.setServiceClass factory busca_cep.adapters.soap_server.CepService)
    (.setServiceBean factory service)
    (.setAddress factory (str "http://localhost:" port "/cep"))
    (let [server (.create factory)]
      (println (str "SOAP server started on http://localhost:" port "/cep?wsdl"))
      server)))
