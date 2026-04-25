(ns busca-cep.http.server
  (:require
   [aleph.http :as aleph]
   [busca-cep.http.router :as router]))

(defonce http-server (atom nil))

(defn start! [adapter]
  (when @http-server
    (.close @http-server))

  (let [handler (router/make-handler adapter)
        server (aleph/start-server handler {:port 3000})]
    (reset! http-server server)
    (println "HTTP server (Aleph) started on http://localhost:3000")))
