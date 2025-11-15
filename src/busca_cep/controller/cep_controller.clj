(ns busca-cep.controller.cep-controller
  (:require [busca-cep.service.cep-service-impl :as impl]
            [busca-cep.service.cep-service :refer [buscar]]
            [ring.util.response :as r]))

(def service (impl/new-service))

(defn buscar-handler [request]
  (let [cep  (get-in request [:path-params :cep])
        resp (buscar service cep)]
    (if resp
      (r/response resp)
      (-> (r/response {:erro "CEP não encontrado"})
          (r/status 404)))))
