(ns busca-cep.ports.cep-port)

(defprotocol CepPort
  "Porta do domínio para consultar CEP."
  (fetch-cep [this cep]
    "Retorna mapa {:cep ... :logradouro ... :uf ...}"))
