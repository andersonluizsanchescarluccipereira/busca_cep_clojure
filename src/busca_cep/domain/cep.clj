(ns busca-cep.domain.cep
  (:require [clojure.string :as str]))

(def cep-regex #"^\d{5}-?\d{3}$")

(defn normalize-cep
  "Remove máscara e valida. Retorna string com 8 dígitos ou nil."
  [cep]
  (when (string? cep)
    (let [digits (str/replace cep #"[^0-9]" "")]
      (when (= 8 (count digits))
        digits))))

(defn valid-cep?
  "Validação 'profissional' básica:
   - aceita com ou sem hífen
   - exatamente 8 dígitos
   - retorna boolean"
  [cep]
  (boolean (and (string? cep)
                (re-matches cep-regex cep))))

;; Exemplo de função de negócio que poderia evoluir:
(defn format-cep
  "Formata '12345678' -> '12345-678'. Retorna nil se input inválido."
  [digits]
  (when (and (string? digits)
             (= 8 (count digits))
             (re-matches #"^\d{8}$" digits))  ;; valida 8 dígitos
    (str (subs digits 0 5) "-" (subs digits 5 8))))
