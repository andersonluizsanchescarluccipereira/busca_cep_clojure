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
  [cep]
  (boolean (and (string? cep)
                (re-matches cep-regex cep))))
(defn format-cep
  [digits]
  (when (and (string? digits)
             (= 8 (count digits))
             (re-matches #"^\d{8}$" digits))  ;; valida 8 dígitos
    (str (subs digits 0 5) "-" (subs digits 5 8))))
