(ns busca-cep.adapters.kafka-consumer
  (:require [busca-cep.ports.cep-port :as port]
            [cheshire.core :as json])
  (:import [org.apache.kafka.clients.consumer KafkaConsumer ConsumerRecords]
           [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.avro Schema$Parser]
           [org.apache.avro.generic GenericData$Record GenericDatumReader GenericDatumWriter]
           [org.apache.avro.io DecoderFactory EncoderFactory]
           [java.io ByteArrayOutputStream]
           [java.util Properties]))

(defn- create-consumer []
  (let [props (Properties.)]
    (.put props "bootstrap.servers" "localhost:9092")
    (.put props "group.id" "cep-consumer")
    (.put props "key.deserializer" "org.apache.kafka.common.serialization.StringDeserializer")
    (.put props "value.deserializer" "org.apache.kafka.common.serialization.ByteArrayDeserializer")
    (KafkaConsumer. props)))

(defn- create-producer []
  (let [props (Properties.)]
    (.put props "bootstrap.servers" "localhost:9092")
    (.put props "key.serializer" "org.apache.kafka.common.serialization.StringSerializer")
    (.put props "value.serializer" "org.apache.kafka.common.serialization.ByteArraySerializer")
    (KafkaProducer. props)))

(defn- deserialize-avro [data schema]
  (let [reader (GenericDatumReader. schema)
        decoder (.binaryDecoder (DecoderFactory/get) data nil)]
    (.read reader nil decoder)))

(defn- serialize-avro [record schema]
  (let [writer (GenericDatumWriter. schema)
        out (ByteArrayOutputStream.)
        encoder (.binaryEncoder (EncoderFactory/get) out nil)]
    (.write writer record encoder)
    (.flush encoder)
    (.toByteArray out)))

(defn start-kafka-consumer [adapter request-topic response-topic]
  (let [consumer (create-consumer)
        producer (create-producer)
        request-schema (-> (slurp "resources/cep.avsc") (json/parse-string) first (Schema$Parser.) .parse)
        response-schema (-> (slurp "resources/cep.avsc") (json/parse-string) second (Schema$Parser.) .parse)]
    (.subscribe consumer [request-topic])
    (future
      (loop []
        (let [records (.poll consumer 1000)]
          (doseq [record records]
            (try
              (let [data (.value record)
                    request (deserialize-avro data request-schema)
                    cep (.get request "cep")
                    request-id (.get request "requestId")
                    result (port/fetch-cep adapter cep)
                    response-record (GenericData$Record. response-schema)]
                (.put response-record "cep" (:cep result ""))
                (.put response-record "logradouro" (:logradouro result ""))
                (.put response-record "complemento" (:complemento result ""))
                (.put response-record "bairro" (:bairro result ""))
                (.put response-record "localidade" (:localidade result ""))
                (.put response-record "uf" (:uf result ""))
                (.put response-record "ibge" (:ibge result ""))
                (.put response-record "gia" (:gia result ""))
                (.put response-record "ddd" (:ddd result ""))
                (.put response-record "siafi" (:siafi result ""))
                (.put response-record "erro" (boolean (:erro result)))
                (.put response-record "requestId" request-id)
                (let [serialized (serialize-avro response-record response-schema)
                      producer-record (ProducerRecord. response-topic request-id serialized)]
                  (.send producer producer-record)))
              (catch Exception e
                (println "Error processing Kafka message:" e)))))
        (recur)))
    (println "Kafka consumer started")))
