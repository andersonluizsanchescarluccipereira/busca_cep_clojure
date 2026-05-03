(defproject busca-cep "0.1.0-SNAPSHOT"
  :description "Serviço de consulta de CEP (Arquitetura Hexagonal)"
  :url "https://example.com/busca-cep"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  ;; -------------------------
  ;; DEPENDÊNCIAS PRINCIPAIS
  ;; -------------------------
  :dependencies [;; Clojure
                 [org.clojure/clojure "1.11.1"]

                 ;; HTTP Server
                 [ring/ring-core "1.11.0"]
                 [ring/ring-jetty-adapter "1.11.0"]

                 ;; Roteamento
                 [metosin/reitit "0.7.0"]
                 [metosin/muuntaja "0.6.8"]

                 ;; HTTP Client (adapter ViaCEP)
                 [clj-http "3.12.3"]

                 ;; JSON
                 [cheshire "5.11.0"]


                 ;; Integrant para DI / Wiring
                 [integrant "0.8.0"]
                 [software.amazon.awssdk/dynamodb "2.25.54"]
                 [software.amazon.awssdk/url-connection-client "2.25.54"]]

  ;; -------------------------
  ;; PLUGINS
  ;; -------------------------
  :plugins [[lein-cloverage "1.2.4"]]

  ;; -------------------------
  ;; ENTRYPOINT DO SISTEMA
  ;; -------------------------
  ;; Rodará a função -main em src/busca_cep/main.clj
  :main ^:skip-aot busca-cep.main

  ;; -------------------------
  ;; ONDE FICA O BUILD
  ;; -------------------------
  :target-path "target/%s"

  ;; -------------------------
  ;; PERFIL DE PRODUÇÃO / UBERJAR
  ;; -------------------------
  :profiles
  {:uberjar {:aot :all
             ;; melhora performance do jar
             :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}

   ;; Perfil de desenvolvimento (opcional)
   :dev {:dependencies [[integrant/repl "0.3.3"]
                        [org.clojure/tools.namespace "1.4.5"]
                        [org.clojure/clojure "1.11.1"]
                        [ring/ring-mock "0.4.0"]]
         :plugins [[lein-cloverage "1.2.4"]]
         :source-paths ["src" "test"]
         :resource-paths ["resources"]
         }})
