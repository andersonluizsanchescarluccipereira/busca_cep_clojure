(ns busca-cep.infra.dynamo
  (:import
   (software.amazon.awssdk.services.dynamodb DynamoDbClient)
   (software.amazon.awssdk.regions Region))
  (:import (java.net URI)))

(defn client []
  (-> (DynamoDbClient/builder)
      (.endpointOverride (URI. "http://localhost:4566"))
      (.region (Region/US_EAST_1))
      .build))
