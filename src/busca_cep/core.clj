(ns busca-cep.core
  (:require [busca-cep.ports.cep-port :as port]))

(defn consultar-cep
  [{:keys [cep-port]} cep]
  (port/fetch-cep cep-port cep))
