(ns busca-cep.system-test
  (:require
    [clojure.test :refer :all]
    [busca-cep.system :as system]
    [busca-cep.adapters.via-cep :as via]
    [busca-cep.http.server :as server]
    [busca-cep.adapters.dynamo-cache :as cache]))

(deftest build-system-test
  (let [sys (system/build-system)]
    (is (contains? sys :adapter))
    (is (contains? sys :http-server))
    (is (fn? (:http-server sys)))))

(deftest start!-test
  (let [adapter-dummy {:dummy "adapter"}
        server-called (atom nil)]

    (with-redefs [via/new-adapter (fn [] adapter-dummy)
                  server/start! (fn [adapter]
                                  (reset! server-called adapter))
                  cache/new-cache (fn [adapter _ _] adapter)]

      (system/start!)

      (is (= adapter-dummy @server-called))
      (is (= adapter-dummy (:adapter @system/system-state)))
      (is (contains? @system/system-state :http-server)))))

(deftest stop!-test
  (with-redefs [println (fn [& _] nil)]
    (reset! system/system-state {:adapter :whatever})
    (system/stop!)
    (is (nil? @system/system-state))))
