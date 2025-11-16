(ns busca-cep.main-test
  (:require [clojure.test :refer :all]
            [busca-cep.main :refer :all]
            [busca-cep.system :as system]))

(deftest main-calls-start!
  (let [called? (atom false)]
    (with-redefs [system/start! (fn [] (reset! called? true))]
      (-main)
      (is @called?))))
