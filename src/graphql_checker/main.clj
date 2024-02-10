(ns graphql-checker.main
  (:gen-class)
  (:require
   [clj-antlr.core :as antlr]
   [cuerdas.core :as str]
   [com.walmartlabs.lacinia.parser.common :as common]
   [datascript.core :as d]
   [clojure.java.io :as io]))

;; Load Lacinia's GraphQL Schema
(def grammar-schema (common/compile-grammar
                     "com/walmartlabs/lacinia/schema.g4"))

(def grammar-graphql (common/compile-grammar
                      "com/walmartlabs/lacinia/Graphql.g4"))

(defn detect-type
  [x]
  (::namespace (meta x)))

(defmulti to-sql #'detect-type)

(defn typed-map
  [namespace values]
  (with-meta
    (reduce-kv (fn [acc key value]
                 (assoc acc
                        (keyword (name namespace) (name key))
                        value))
               {}
               values)
    {::namespace namespace}))

(def position    (partial typed-map :position))
(def description (partial typed-map :description))
(def name-token  (partial typed-map :name-token))
(def type-spec   (partial typed-map :type-spec))
(def field-def   (partial typed-map :field-def))
(def fields      (partial typed-map :fields))
(def list-type   (partial typed-map :list-type))
(def type-name   (partial typed-map :type-name))
(def type-def    (partial typed-map :type-def))
(def argument    (partial typed-map :argument))
(def arg-list    (partial typed-map :arg-list))
(def graphql-schema (partial typed-map :graphql-schema))

(defn get-position
  "Given an object with a poisiton metadata
   Returns the meta data"
  [args]
  (let [antlr (meta args)]
    (when (:clj-antlr/position antlr)
      (position
       (:clj-antlr/position antlr)))))

(comment
  :graphqlSchema
  :typeDef
  :typeSpec
  :fieldDef
  :anyName
  :nameTokens)

;; Taken and simplifed from Lacina parser.schema
(defmulti xform
  #'first)

(defmethod xform :default [prod]
  prod)

(def drop-string-xform
  (comp
   (remove string?)
   (remove keyword?)
   (map xform)
   (remove nil?)))

(defn- group-info
  [type-info]
  (group-by detect-type type-info))

(defn- prepare-parse-production
  [parsed-form]
  (into []
         drop-string-xform
         parsed-form))

(defn- all
  [type-info type-name]
  (get type-info type-name))

(defn- one
  [type-info type-name]
  (first (all type-info type-name)))

(defmethod xform :nameTokens
  [[_ token :as args]]
  (name-token
   {:name     (keyword token)
    :position (get-position args)}))

(defmethod xform :description
  [[_ description-value :as args]]
  (description
   {:value (->
            description-value
            (str/strip-newlines)
            (str/replace "\"" "")
            (str/trim))
    :position (get-position args)}))

(defmethod xform :anyName
  [[_ name-tokens]]
  (xform name-tokens))

(defmethod xform :required
  [[_ value :as _args]]
  (= value "!"))

(comment
  '(:implementationDef "implements" "Sentient"))

(defmethod xform :implementationDef
  [[_ operation & types]]
  [(keyword operation)
   (remove #{"&"} types)])

(defmethod xform :typeSpec
  [[_ type-name require :as args]]
  (type-spec
   {:type-name (xform type-name)
    :required  (xform require)
    :position  (get-position args)}))

(defmethod xform :argument
  [parse-prod]
  (let [type-info (group-info (prepare-parse-production parse-prod))
        arg-name  (one type-info :name-token)
        type-spec (one type-info :type-spec)]
    (argument {:argument-name arg-name
               :type-spec     type-spec
               :position      (get-position parse-prod)})))

(defmethod xform :argList
  [parse-production]
  (arg-list
   {:arguments (into []
                     drop-string-xform
                     parse-production)
    :position  (get-position parse-production)}))

(defmethod xform :fieldDef
  [args]
  (let [type-info  (group-info (prepare-parse-production args))
        field-name (one type-info :name-token)
        type-spec  (one type-info :type-spec)]

    (field-def
     {:field-name field-name
      :type-spec  type-spec
      :position   (get-position args)})))

(defmethod xform :typeName
  [[_ any-name :as args]]
  (type-name
   {:name     (xform any-name)
    :position (get-position args)}))

(defmethod xform :fieldDefs
  [[_ & rest-prod :as args]]
  (fields
   {:fields   (into []
                  drop-string-xform
                  rest-prod)
    :position (get-position args)}))

(defmethod xform :listType
  [[_ & rest-prod :as args]]
  (list-type
   {:type-spec (into []
                     drop-string-xform
                     rest-prod)
    :position (get-position args)}))

(defmethod xform :typeDef
  [prod]
  (let [values
        (into []
              drop-string-xform
              prod)
        type-info   (group-info values)
        ;; There is only one name token per type
        type-name-value   (one type-info :name-token)
        ;; Assume there there is only one description per type for
        ;; now.
        description (one type-info :description)]
    (type-def
     {:type-name   type-name-value
      :description description
      :fields      (get type-info :fields)
      :implements  nil
      :position    (get-position prod)})))

(defmethod xform :graphqlSchema
  [[_ graphql-schema :as args]]
  (graphql-schema
   {:schema   (xform graphql-schema)
    :position (get-position args)}))

(comment
  (ns-unmap *ns* 'xform))

(defn parse-schema
  [path]
  (let [gql-string    (slurp (io/resource path))
        parsed-schema (antlr/parse grammar-schema {:throw? false} gql-string)]
    parsed-schema))

(defn -main [& _args]
  (parse-schema "test-data/example.graphql"))
