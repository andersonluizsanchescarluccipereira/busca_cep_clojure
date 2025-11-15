(ns busca-cep.service.cep-service-impl
  (:require [busca-cep.service.cep-service :refer [CepService]]
            [busca-cep.component.via-cep-client :as client]))

(defrecord CepServiceImpl []
  CepService
  (buscar [_ cep]
    (client/buscar-cep cep)))

(defn new-service []
  (->CepServiceImpl))
