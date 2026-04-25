(ns busca-cep.http.server-test
  (:require [clojure.test :refer :all]
            [busca-cep.http.server :as server]
            [ring.adapter.jetty :as jetty]))

(deftest start-server-test
  (let [called (atom nil)]
    (with-redefs [jetty/run-jetty
                  (fn [handler opts]
                    (reset! called {:handler handler :opts opts})
                    (proxy [org.eclipse.jetty.server.Server] []
                      (stop []
                        (println "Stop called"))))]

      (server/start! nil)

      (is (= 3000 (:port (:opts @called))))
      (is (= false (:join? (:opts @called))))
      (is (some? @server/http-server)))))
