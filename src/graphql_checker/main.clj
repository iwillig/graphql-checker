(ns graphql-checker.main
  (:gen-class)
  (:require
   [clj-antlr.core]
   [clj-commons.format.exceptions :as exceptions]
   [clojure.java.io :as io]
   [clojure.stacktrace :as stacktrace]
   [com.walmartlabs.lacinia.parser.common :as common]
   [com.walmartlabs.lacinia.parser.schema :as schema]
   [puget.printer :as puget]))

;; Load Lacinia's GraphQL Schema
(def grammar (common/compile-grammar "com/walmartlabs/lacinia/schema.g4"))

(defn parse-schema
  [_path]
  (println grammar))

(defn -main [& _args]
  (parse-schema nil))
