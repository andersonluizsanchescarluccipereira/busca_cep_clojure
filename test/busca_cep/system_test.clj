(ns busca-cep.system-test
  (:require
   [clojure.test :refer :all]
   [busca-cep.system :as system]
   [busca-cep.adapters.via-cep :as via]
   [busca-cep.http.server :as server]
   [busca-cep.adapters.dynamo-cache]))


(deftest build-system-test
  (let [sys (system/build-system)]
    (is (contains? sys :adapter))
    (is (contains? sys :server))
    (is (fn? (:server sys)))))

(deftest start!-test
  (let [adapter-called (atom nil)
        adapter-dummy {:dummy "adapter"}
        server-called (atom nil)]

    (with-redefs [via/new-adapter (fn [] adapter-dummy)
                  server/start! (fn [adapter]
                                  (reset! server-called adapter))
                  busca-cep.adapters.dynamo-cache/new-cache (fn [adapter _ _] adapter)] ; mock cache to return adapter as is
      (system/start!)
      (is (= adapter-dummy @server-called))
      (is (= {:adapter adapter-dummy} @system/system-state)))))

(deftest stop!-test
  (with-redefs [println (fn [& _] nil)] ; silencia o println
    (reset! system/system-state {:adapter :whatever})
    (system/stop!)
    (is (nil? @system/system-state))))
