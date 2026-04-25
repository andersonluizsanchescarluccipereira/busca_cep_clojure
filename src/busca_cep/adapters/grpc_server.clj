(ns busca-cep.adapters.grpc-server
  (:import [io.grpc Server ServerBuilder]
           [busca_cep CepServiceGrpc CepServiceGrpc$CepServiceImplBase CepRequest CepResponse]
           [io.grpc.stub StreamObserver])
  (:require [busca-cep.ports.cep-port :as port]))

(defn- build-response [cep-data]
  (let [builder (CepResponse/newBuilder)]
    (when cep-data
      (.setCep builder (:cep cep-data ""))
      (.setLogradouro builder (:logradouro cep-data ""))
      (.setComplemento builder (:complemento cep-data ""))
      (.setBairro builder (:bairro cep-data ""))
      (.setLocalidade builder (:localidade cep-data ""))
      (.setUf builder (:uf cep-data ""))
      (.setIbge builder (:ibge cep-data ""))
      (.setGia builder (:gia cep-data ""))
      (.setDdd builder (:ddd cep-data ""))
      (.setSiafi builder (:siafi cep-data ""))
      (.setErro builder (boolean (:erro cep-data))))
    (.build builder)))

(defn cep-service-impl [adapter]
  (proxy [CepServiceGrpc$CepServiceImplBase] []
    (fetchCep [^CepRequest request ^StreamObserver responseObserver]
      (try
        (let [cep (.getCep request)
              result (port/fetch-cep adapter cep)
              response (build-response result)]
          (.onNext responseObserver response)
          (.onCompleted responseObserver))
        (catch Exception e
          (.onError responseObserver e))))))

(defn start-grpc-server [adapter port]
  (let [server (-> (ServerBuilder/forPort port)
                   (.addService (cep-service-impl adapter))
                   .build)]
    (.start server)
    (println (str "gRPC server started on port " port))
    server))
