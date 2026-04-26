(ns busca-cep.http.server
  (:require
   [ring.adapter.jetty :as jetty]
   [busca-cep.http.router :as router]))

(defonce http-server (atom nil))

(defn start! [adapter]
  (when @http-server
    (.stop @http-server))

  (let [handler (router/make-handler adapter)
        server (jetty/run-jetty handler {:port 3000 :join? false})]
    (reset! http-server server)
    (println "HTTP server started on http://localhost:3000")))
