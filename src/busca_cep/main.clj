(ns busca-cep.main
  (:require [busca-cep.system :as system])
  (:gen-class))

(defn -main [& _]
  (system/start!))
