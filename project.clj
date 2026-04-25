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

                 ;; gRPC
                 [io.grpc/grpc-core "1.50.0"]
                 [io.grpc/grpc-protobuf "1.50.0"]
                 [io.grpc/grpc-stub "1.50.0"]
                 [com.google.protobuf/protobuf-java "3.21.7"]

                 ;; Kafka
                 [org.apache.kafka/kafka-clients "3.3.1"]

                 ;; Avro
                 [org.apache.avro/avro "1.11.1"]

                 ;; WebSocket/SSE
                 [aleph "0.4.7-alpha10"]  ;; for async HTTP, WebSocket, SSE

                 ;; SOAP
                 [org.apache.cxf/cxf-rt-frontend-jaxws "3.5.5"]
                 [org.apache.cxf/cxf-rt-transports-http "3.5.5"]

                 ;; Integrant para DI / Wiring
                 [integrant "0.8.0"]
                 [software.amazon.awssdk/dynamodb "2.25.54"]
                 [software.amazon.awssdk/url-connection-client "2.25.54"]]

  ;; -------------------------
  ;; PLUGINS
  ;; -------------------------
  :plugins [[lein-cloverage "1.2.4"]
            [lein-protobuf "0.5.0"]]

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
   :dev {:dependencies [[ring/ring-mock "0.4.0"]
                        [integrant/repl "0.3.3"]
                        [org.clojure/tools.namespace "1.4.5"]]}}

  ;; Protobuf
  :protobuf {:proto-path "resources" :java-out "src/java"})
