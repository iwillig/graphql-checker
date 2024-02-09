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

(defrecord Position [row column index stop])

(defrecord NameToken [name position])

(defrecord TypeSpec  [type-name requied position])

(defrecord FieldDef  [field-name type-spec position])

(defrecord TypeDef   [type-name fields implements position])

(defrecord ListType  [type-sec position])

(comment
  :graphqlSchema
  :typeDef
  :typeSpec
  :fieldDef
  :anyName
  :nameTokens)

(comment
  {:type-def/name       "Human"
   :type-def/implements {}
   :type-def/fields     #{}}

  {:field-def/name      "name"
   :field-def/type-name "String"
   :field-def/required  true}

  '(:fieldDef
    (:anyName (:nameTokens "name"))
    ":"
    (:typeSpec
     (:typeName (:anyName (:nameTokens "String")))
     (:required !)))

  (:typeSpec
   (:typeName (:anyName (:nameTokens "String")))
   (:required "!")))

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
  [[_ token :as args]]
  (map->NameToken
   {:name token
    :position (map->Position (meta args))}))

(defmethod xform :anyName
  [[_ name-tokens]]
  (xform name-tokens))

(comment
  '(:implementationDef "implements" "Sentient"))

(defmethod xform :implementationDef
  [[_ operation & types]]
  [(keyword operation)
   (remove #{"&"} types)])

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
  rest-prod)

(defmethod xform :listType
  [[_ & rest-prod]]
  [:listType
   (into []
         drop-string-xform
         rest-prod)])

(comment
    [:typeDef
     (map
      maybe-xform
      type-info)]
  )

(defmethod xform :typeDef
  [prod]
  (let [type-info (nthrest prod 2)]
    #_(map->TypeDef
       {})
    (map
     maybe-xform
     type-info)
    ))

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
  (parse-schema "test-data/example.graphql"))
