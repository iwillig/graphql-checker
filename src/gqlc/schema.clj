(ns gqlc.schema
  (:require
   [malli.error :as me]
   [malli.core :as m]
   [malli.generator :as mg]))

(def FormPosition
  [:map
   [:position/row pos?]
   [:position/column pos?]
   [:position/index pos?]
   [:position/stop pos?]])


(def FormError
  [:map ])

(def Description
  [:map
   [:description/value string?]
   [:form/position FormPosition]])


(def NameToken
  [:map
   [:name-token/value keyword?]
   [:form/position FormPosition]])

(def Required
  [:map
   [:required/value boolean?]
   [:form/position FormPosition]])

(def Implements
  [:map
   [:implement/operation string?]
   [:implement/type-values [:vector string?]]
   [:form/position FormPosition]])

(def ListTypeName
  [:map
   [:list-name-token/list-of [:vector keyword?]]
   [:form/position FormPosition]])

(def TypeSpec
  [:map
   [:type-spec/name [:or NameToken ListTypeName]]
   [:type-spec/required Required]
   [:form/position FormPosition]])

(def ListType
  [:map
   [:list-type/type-spec TypeSpec]
   [:form/position FormPosition]])

(def Argument
  [:map
   [:argument/argument-name NameToken]
   [:argument/type-spec     TypeSpec]
   [:form/position          FormPosition]])

(def ArgList
  [:map
   [:arg-list/arguments [:vector Argument]]
   [:form/position FormPosition]])

(def TypeName
  [:map
   [:type-name/name NameToken]
   [:form/position FormPosition]])

(def FieldDef
  [:map
   [:field-def/field-name NameToken]
   [:field-def/type-sepc TypeSpec]
   [:form/position FormPosition]])

(def FieldDefs
  [:map
   [:fields/fields [:vector FieldDef]]
   [:form/position FormPosition]])

(def TypeDef
  [:map
   [:type-def/type-name NameToken]
   [:type-def/description Description]
   [:type-def/fields FieldDefs]
   [:type-def/implements Implements]
   [:form/position FormPosition]
   [:form/error    FormError]])

(def QueryDef
  [:map])

(def MutationDef
  [:map])

(def GraphQlSchema
  [:map
   [:graphql-schema/types [:vector TypeDef]]
   [:graphql-schema/queries [:vector QueryDef]]
   [:graphql-schema/mutations [:vector MutationDef]]
   [:form/position FormPosition]])


(comment


  (mg/generate ListType)

  (mg/generate GraphQlSchema)


  (m/validate ListType (mg/generate ListType))

  )
