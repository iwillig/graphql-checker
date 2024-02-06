(ns graphql-checker.main
  (:gen-class)
  (:require
   [clj-antlr.core :as antlr]
   ;;[clj-commons.format.exceptions :as exceptions]
   ;;[clojure.stacktrace :as stacktrace]
   [com.walmartlabs.lacinia.parser.common :as common]
   ;; [com.walmartlabs.lacinia.parser.schema :as schema]
   ;;[puget.printer :as puget]

   [clojure.java.io :as io]))

;; Load Lacinia's GraphQL Schema
(def grammar (common/compile-grammar
              "com/walmartlabs/lacinia/schema.g4"))

(defrecord NameToken [name-value])
(defrecord TypeSpec  [type-name requied])
(defrecord FieldDef  [field-name type-spec])
(defrecord TypeDef   [type-name fields])
(defrecord ListType  [type-sec])


(comment

  :graphqlSchema

  :typeDef
  :typeSpec
  :fieldDef


  :anyName
  :nameTokens


  )

;; Taken and simplifed from Lacina parser.schema
(defmulti xform
  first)

(defmethod xform :default [prod]
  prod)

(defn maybe-xform
  [x]
  (cond (string? x) x
        (seqable? x)
        (xform x)
        :else x))

(defmethod xform :nameTokens
  [[_ token]]
  token)

(defmethod xform :anyName
  [[_ name-tokens]]
  (xform name-tokens))

(comment
  '(:implementationDef implements Sentient))

(defmethod xform :implementationDef
  [[_ operation & types]]
  [(keyword operation)
   (remove #{"&"} types)])


(comment
  '(:fieldDef
    (:anyName (:nameTokens "name"))
    ":"
    (:typeSpec
     (:typeName (:anyName (:nameTokens "String")))
     (:required !))))

(comment
  (:typeSpec
   (:typeName (:anyName (:nameTokens "String")))
   (:required "!")))

(def drop-string-xform
  (comp
   (remove string?)
   (map xform)
   (remove nil?)))

(defmethod xform :typeSpec
  [[_ type-name require]]
  [(xform type-name) (xform require)])

(defmethod xform :fieldDef
  [[_ field-name _ type-spec]]
  [(xform field-name) (xform type-spec)])

(defmethod xform :typeName
  [[_ any-name]]
  (xform any-name))

(defmethod xform :fieldDefs
  [[_ & rest-prod]]
  (into []
        drop-string-xform
        rest-prod))

(defmethod xform :listType
  [[_ & rest-prod]]
  [:listType
   (into []
         drop-string-xform
         rest-prod)])

(defmethod xform :typeDef
  [prod]
  (let [type-info (nthrest prod 2)]
    [:typeDef
     (map
      maybe-xform
      type-info)]))


(defn parse-schema
  [path]
  (let [gql-string    (slurp (io/resource path))
        parsed-schema (antlr/parse grammar {:throw? false} gql-string)]
    parsed-schema))

(defn- xform-schema
  [schema-tree]
  (->> schema-tree
       (rest)
       (map xform)))

(defn -main [& _args]
  (xform-schema
   (parse-schema "test-data/example.graphql")))
