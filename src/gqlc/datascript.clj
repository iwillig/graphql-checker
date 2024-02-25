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


;; :db/cardinality :db.cardinality/many

(def db-schema
  "Datascript schema.

  Defines all of the possbile attributes
  "
  {
   ;; Position
   :position/row
   {}
   :position/column
   {}
   :position/index
   {}
   :position/stop
   {}

   ;; Form
   :form/position {:db/valueType :db.type/ref}

   ;; Type Description
   :description/value {}

   })
