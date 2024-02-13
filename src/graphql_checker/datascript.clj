(ns graphql-checker.datascript
  (:require [datascript.core :as d]))


(comment

  {:db/id -1
   :position/row 27
   :position/column 9
   :position/index 507
   :position/stop 511}

  {:description/value ""
   :gql-form/position -1}

  {:name-token/value :Person
   :gql-form/position -1}

  {:db/id -2
   :type-name/name ""
   :gql-form/position -1}

  {:required/value true
   :gql-form/position -1}


  {:type-spec/name -2
   :type-spec/required? true
   :gql-form/position -1}



  )



(def db-schema
  "Datascript schema.

  Defines all of the possbile attributes
  "
  {})
