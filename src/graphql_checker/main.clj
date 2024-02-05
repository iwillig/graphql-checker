(ns graphql-checker.main
  (:gen-class)
  (:require
   [clj-antlr.core :as antlr]
   ;;[clj-commons.format.exceptions :as exceptions]
   ;;[clojure.stacktrace :as stacktrace]
   [com.walmartlabs.lacinia.parser.common :as common]
   ;;[com.walmartlabs.lacinia.parser.schema :as schema]
   ;;[puget.printer :as puget]

   [clojure.java.io :as io]))

;; Load Lacinia's GraphQL Schema
(def grammar (common/compile-grammar "com/walmartlabs/lacinia/schema.g4"))

(defn parse-schema
  [path]
  (let [gql-string    (slurp (io/resource path))
        parsed-schema (antlr/parse grammar {:throw? false} gql-string)]
    parsed-schema))

(defn -main [& _args]
  (parse-schema "test-data/example.graphql"))
