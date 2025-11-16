(ns busca-cep.http.server-test
  (:require
   [clojure.test :refer :all]
   [busca-cep.http.server :as server]
   [ring.adapter.jetty :as jetty]))

(deftest start-server-test
  (let [called (atom nil)
        fake-server {:fake "server"}]

    ;; mocka run-jetty da lib jetty
    (with-redefs [jetty/run-jetty
                  (fn [handler opts]
                    (reset! called {:handler handler :opts opts})
                    fake-server)
                  ;; mock para o método .stop do fake-server
                  ;; Como no seu código chama (.stop @http-server), precisamos que o fake-server tenha esse método
                  ]

      (server/start! nil)

      (is (= 3000 (:port (:opts @called))))
      (is (= false (:join? (:opts @called))))
      (is (= fake-server @server/http-server)))))
