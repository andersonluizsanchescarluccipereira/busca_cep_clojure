(ns busca-cep.model.cep-dto)

(defn ->CepDTO [data]
  {:cep         (:cep data)
   :logradouro  (:logradouro data)
   :bairro      (:bairro data)
   :localidade  (:localidade data)
   :uf          (:uf data)})
