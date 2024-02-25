(ns gqlc.datascript
  (:require
   [datascript.core :as d]))

(comment
  (d/q ))

(comment

  {:db/id           -1
   :position/row    27
   :position/column 9
   :position/index  507
   :position/stop   511}

  {:type-description/value
   ""
   :form/position          -1}

  {:name-token/value :Person
   :form/position    -1}

  {:db/id          -2
   :type-name/name ""
   :form/position  -1}

  {:required/value true
   :form/position  -1}

  {:type-spec/name      -2
   :type-spec/required? true
   :form/position       -1}

  )

(comment
  {:row    (dec (.getLine start-token))
   :column (.getCharPositionInLine start-token)
   :index  (.getStartIndex start-token)
   :stop   (.getStopIndex stop-token)})


;; Clojure/DataScript core entity types
;; --------------------------------------------------

;; position [DONE]
;; description [TODO]
;; name-token [TODO]
;; list-name-token [TODO]
;; type-spec [TODO]
;; field-def [TODO]
;; fields  [TODO]
;; list-type [TODO]
;; type-def
;; argument
;; arg-list
;; implements
;; required
;; graphql-schema


(def db-schema
  "Datascript schema.

  Defines all of the possbile attributes
  "
  {
   ;; Position
   :position/row
   {:db/doc
    "The line number or 'row' of the expression.
     Implemented in clj-antrl as `(dec (.getLine start-token))`"}

   :position/column
   {:db/doc
    "The column of the expression
     Implemented in clj-antrl as `(.getCharPositionInLine start-token)`"}

   :position/index
   {:db/doc ""}

   :position/stop
   {:db/doc "The end of the GraphQL Expression"}

   ;; Form
   :form/position
   {:db/valueType :db.type/ref
    :db/doc ""}

   ;; Type Description
   :description/value
   {:db/doc ""}

   })





(defn create-schema-db
  "Given no args
   Returns a new datascript connection object"
  []
  (d/create-conn db-schema))

(comment
  (defonce db (create-schema-db)))

(def db (create-schema-db))
