(ns gqlc.main
  (:gen-class)
  (:require
   [clj-antlr.core :as antlr]
   [cuerdas.core :as str]
   [com.walmartlabs.lacinia.parser.common :as common]
   [clojure.core.match :refer [match]]
   ;;[datascript.core :as d]
   [clojure.java.io :as io])
  (:import
   (java.util.concurrent.atomic AtomicLong)))

(set! *warn-on-reflection* true)

;; Load Lacinia's GraphQL Schema
(def grammar-schema (common/compile-grammar
                     "com/walmartlabs/lacinia/schema.g4"))

(def grammar-graphql (common/compile-grammar
                      "com/walmartlabs/lacinia/Graphql.g4"))

(defn detect-namespace
  "Function that can be used multimethods to correct detect the
  entites's namespace."
  [x]
  (::namespace (meta x)))

(defn namespace-map
  "Given a keyword of a namespace, and a normal clojure map
   Returns the clojure map with the keys namespaced with the provided
  namespace"
  [namespace values]
  (with-meta
    (reduce-kv (fn [acc key value]

                 (assoc acc
                        (keyword
                         (condp = key
                           :position "form"
                           :error    "form"
                           (name namespace))
                          (name key))
                        value))
               {}
               values)
    {::namespace namespace}))

(def position        (partial namespace-map :position))
(def description     (partial namespace-map :description))
(def name-token      (partial namespace-map :name-token))
(def list-name-token (partial namespace-map :list-name-token))

(def type-spec   (partial namespace-map :type-spec))
(def field-def   (partial namespace-map :field-def))
(def fields      (partial namespace-map :fields))
(def list-type   (partial namespace-map :list-type))
(def type-name   (partial namespace-map :type-name))
(def type-def    (partial namespace-map :type-def))
(def argument    (partial namespace-map :argument))
(def arg-list    (partial namespace-map :arg-list))
(def implements  (partial namespace-map :implements))
(def required    (partial namespace-map :required))


(def graphql-schema (partial namespace-map :graphql-schema))

(defn get-position
  "Given an object with a poisiton metadata
   Returns the meta data"
  [args]
  (let [antlr (meta args)]
    (when (:clj-antlr/position antlr)
      (position
       (:clj-antlr/position antlr)))))

(defmulti to-sql #'detect-namespace)

(comment
  :graphqlSchema
  :typeDef
  :typeSpec
  :fieldDef
  :anyName
  :nameTokens)


;; Taken and simplifed from Lacina parser.schema
(defmulti ast->clj
  "Given an AST of a GraphQL form

   Transform and return that AST struture into namespaced clojure maps"
  #'first)

;; Default method
(defmethod ast->clj :default [prod]
  prod)

(def drop-string-xform
  (comp
   (remove string?)
   (remove keyword?)
   (map ast->clj)
   (remove nil?)))

(defn- group-info
  [type-info]
  (group-by detect-namespace type-info))

(defn- prepare-parse-production
  [parsed-form]
  (into []
         drop-string-xform
         parsed-form))

(defn- prep-and-group-production
  [parsed-form]
  (-> (prepare-parse-production parsed-form)
      (group-info)))

(defn- all
  [type-info type-name]
  (get type-info type-name))

(defn- one
  [type-info type-name]
  (first (all type-info type-name)))

(defmethod ast->clj :nameTokens
  [[_ token :as args]]
  (name-token
   {:value    (keyword "name-token.value" token)
    :position (get-position args)}))

(defmethod ast->clj :description
  [[_ description-value :as args]]
  (description
   {:value (->
            description-value
            (str/strip-newlines)
            (str/replace "\"" "")
            (str/trim))
    :position (get-position args)}))

(defmethod ast->clj :anyName
  [[_ name-tokens]]
  (ast->clj name-tokens))

(defmethod ast->clj :required
  [[_ value :as args]]
  (required {:value (= value "!")
             :position (get-position args)}))

(comment
  '(:implementationDef "implements" "Sentient"))

(defmethod ast->clj :implementationDef
  [[_ operation & types]]
  (implements {:operation operation
               :type-values (mapv keyword (remove #{"&"} types))}))

(defn list-type-name
  [list-type]
  (list-name-token {:list-of (get-in list-type [:list-type/type-spec
                                                :type-spec/name
                                                :type-name/name
                                                :name-token/value])}))

(defmethod ast->clj :typeSpec
  [args]
  (let [type-info (group-info (prepare-parse-production args))

        type-name (one type-info :type-name)
        required  (one type-info :required)

        list-type (one type-info :list-type)
        type-spec (type-spec
                   {:name (match [(some? type-name) (some? list-type)]
                                 [true false] type-name
                                 [false true] (list-type-name list-type))
                    :required  required
                    :position  (get-position args)
                    :list-type list-type})]
    type-spec))

(defmethod ast->clj :argument
  [parse-prod]
  (let [type-info (group-info (prepare-parse-production parse-prod))
        arg-name  (one type-info :name-token)
        type-spec (one type-info :type-spec)]
    (argument {:argument-name arg-name
               :type-spec     type-spec
               :position      (get-position parse-prod)})))

(defmethod ast->clj :argList
  [parse-production]
  (arg-list
   {:arguments (into []
                     drop-string-xform
                     parse-production)
    :position  (get-position parse-production)}))

(defmethod ast->clj :fieldDef
  [args]
  (let [type-info  (group-info (prepare-parse-production args))
        field-name (one type-info :name-token)
        type-spec  (one type-info :type-spec)
        description (one type-info :description)]
    (field-def
     {:field-name field-name
      :type-spec  type-spec
      :description description
      :position   (get-position args)})))

(defmethod ast->clj :typeName
  [[_ any-name :as args]]
  (type-name
   {:name     (ast->clj any-name)
    :position (get-position args)}))

(defmethod ast->clj :fieldDefs
  [[_ & rest-prod :as args]]
  (fields
   {:fields   (into []
                  drop-string-xform
                  rest-prod)
    :position (get-position args)}))

(defmethod ast->clj :listType
  [args]
  (let [type-info (group-info (prepare-parse-production args))
        type-spec (one type-info :type-spec)]
    (list-type
     {:type-spec type-spec
      :position (get-position args)})))

(defmethod ast->clj :typeDef
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
        description (one type-info :description)
        implements  (one type-info :implements)]
    (type-def
     {:type-name   type-name-value
      :description description
      :fields      (get type-info :fields)
      :implements  implements
      :position    (get-position prod)})))

(defmethod ast->clj :graphqlSchema
  [[_ graphql-schema-value :as args]]
  (let [parsed-schema (prep-and-group-production args)
        type-defs     (all parsed-schema :type-def)]

    (graphql-schema
     {:types type-defs
      :position (get-position args)})))

(comment
  ;;
  (ns-unmap *ns* 'ast->clj))

(def default-antlr-options
  {:throw? false})

(defn parse-schema-string
  "Given a GraphQL String
   Attempt to parse the graphql forms in the provided string"
  [gql-string]
  (antlr/parse grammar-schema
               default-antlr-options
               gql-string))

(defn parse-&-transform-string
  [gql-string]
  (ast->clj
   (parse-schema-string gql-string)))

(defn parse-schema-file
  [path]
  (let [gql-string    (slurp (io/resource path))]
    (parse-schema-string gql-string)))

;; --------------------------------------------------

(defonce ^:private temp-id
  (AtomicLong. 0))

(defn- next-temp-id []
  (.decrementAndGet ^AtomicLong temp-id))


(defmulti to-datalog
  "Given a database-values accumulator
  "
  #'detect-namespace)

(defmethod to-datalog :default [graphql-expression]
  graphql-expression)

(defmethod to-datalog :type-def
  [gql-exp]
  (let [{:type-def/keys [type-name description fields]} gql-exp
        type-name-id (next-temp-id)
        description-id (next-temp-id)]

    [(assoc type-name :db/id type-name-id)
     (assoc description :db/id description-id)
     (map to-datalog fields)]))


(defn all-to-datalog
  "Transform all of the GraphQL Expressions in a datalog"
  [graphql-forms]
  (reduce-kv
   (fn [datoms _key value]
     (if (seqable? value)
       (into datoms
             (map to-datalog value))

       (conj datoms (to-datalog value))))
   []
   graphql-forms))


(comment
  (ns-unmap *ns* 'ast->clj)
  (ns-unmap *ns* 'to-datalog)

  )

;; --------------------------------------------------

(defn -main [& _args]
  (parse-schema-file "test-data/example.graphql"))
