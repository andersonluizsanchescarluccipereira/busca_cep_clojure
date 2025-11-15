(ns busca-cep.service.cep-service)

(defprotocol CepService
  (buscar [this cep]))
