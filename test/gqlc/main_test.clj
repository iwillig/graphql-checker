(ns gqlc.main-test
  (:require
   [gqlc.main :as gqlc.main]
   [matcher-combinators.test]
   ;;[matcher-combinators.matchers :as m]
   [clojure.test :as t]
   [clojure.java.io :as io]))

(defn gen-random-antlr-position
  []
  {:row    (rand-int 400)
   :column (rand-int 80)
   :index  (rand-int 1000)
   :stop   (rand-int 100)})

(defn- generate-form
  ([form]
   (generate-form form (gen-random-antlr-position)))
  ([form position]
   (with-meta
     form
     #:clj-antlr{:position position})))


(defn load-grapql-example
  "Given a path on the Java resource path
   Returns a parsed and transformed-graphql structure"
  [path]
  (-> (io/resource path)
      (slurp)
      (gqlc.main/parse-schema-string)))

(def example-ast
  '(:graphqlSchema
    (:typeDef "type"
              (:anyName (:nameTokens "Character"))
              (:fieldDefs "{"
                          (:fieldDef (:anyName (:nameTokens "name")) ":"
                                     (:typeSpec (:typeName (:anyName (:nameTokens "String"))) (:required "!")))
                          (:fieldDef (:anyName (:nameTokens "appearsIn")) ":"
                                     (:typeSpec
                                      (:listType "["
                                                 (:typeSpec (:typeName (:anyName
                                                                        (:nameTokens "Episode")))
                                                            (:required "!"))
                                                 "]")
                                      (:required "!")))
                          "}"))))

(def example-graphql-string
  "
\"\"\"
This is a description for the type Character
\"\"\"

type Character {
  \"\"\"
  This is a description for the name field
  \"\"\"
  name: String!
  \"\"\"
  This is a description for the appearsIn
  \"\"\"
  appearsIn: [Episode!]!
}

type Person {
  name: String
}


")

(t/deftest test-parse-gql-string
  (t/testing "Given: A GraphQL String"
    (let [subject example-graphql-string]
      (t/testing "When: We parse that string"
        (let [results (gqlc.main/parse-schema-string subject)]
          ;; This is more of an example of what the AST looks like
          (t/testing "Then: A valid GraphQL AST is returned"
            (t/is (match? example-ast
                          results))))))))


(t/deftest test-parse-and-transform-graphql-string
  (t/testing "Given: A GraphQL String"
    (let [transformed-graphql (gqlc.main/parse-&-transform-string
                               example-graphql-string)]
      (t/is (match? nil?
                    (gqlc.main/all-to-datalog transformed-graphql))))))





(comment

  (def schema-value
    '(:graphqlSchema
      (:typeDef
       "type"
       (:anyName (:nameTokens "Character"))
       (:fieldDefs
        {
         (:fieldDef
          (:anyName (:nameTokens "name"))
          ":"
          (:typeSpec
           (:typeName (:anyName (:nameTokens "String")))
           (:required !)))
         (:fieldDef
          (:anyName (:nameTokens "appearsIn"))
          ":"
          (:typeSpec
           (:listType
            [
             (:typeSpec
              (:typeName (:anyName (:nameTokens "Episode")))
              (:required "!"))
             ])
           (:required "!")))
         }))))

  )

(t/deftest test-name-tokens
  (t/testing "Given: A name-token-ast form"
    (let [subject
          (generate-form '(:nameTokens "pets")
                         {:row 23, :column 2, :index 360, :stop 367})]
      (t/testing "When: We transform it into Clojure data"
        (let [name-token (gqlc.main/ast->clj subject)]
          (t/is (match? {:name-token/value :name-token.value/pets
                         :name-token/position
                         {:position/row 23
                          :position/column 2
                          :position/index 360
                          :position/stop 367}}
                        name-token)
                "Then: We expect it to be a valid name token object"))))))


(t/deftest test-descriptions
  (t/testing "Given: A description as form"
    (let [subject (with-meta
                    '(:description "\"\"\"\nA dog implements the pet\n\"\"\"")
                    #:clj-antlr{:position {:row 18, :column 0, :index 272, :stop 303}})]
      (t/testing "When: We transform it into clojure data"
        (let [description (gqlc.main/ast->clj subject)]
          (t/testing "Then: We expect it to be a valid description object"
            (t/is (match? nil? description))))))))

(t/deftest test-xform-type-def
  (let [subject '(:typeDef
                  "type"
                  (:anyName (:nameTokens "Human"))
                  (:implementationDef "implements" "Pet" "&" "NickName")
                  (:fieldDefs
                   "{"
                   (:fieldDef
                    (:anyName (:nameTokens "name"))
                    ":"
                    (:typeSpec
                     (:typeName (:anyName (:nameTokens "String")))
                     (:required "!")))
                   (:fieldDef
                    (:anyName (:nameTokens "pets"))
                    ":"
                    (:typeSpec
                     (:listType
                      "["
                      (:typeSpec
                       (:typeName (:anyName (:nameTokens "Pet")))
                       (:required "!"))
                      "]")))
                   "}"))]

    (t/testing "When given a valid type def"
      (t/is (match?
             nil?
             (gqlc.main/ast->clj
              subject))))))

(def mutation-type-examples
  "
\"\"\"
This is a description AddDocResult
\"\"\"
type AddDocResult {
  \"\"\"
  This is a description of the name field
  \"\"\"
  name: String
  \"\"\"
  This is a description of the nickName field
  \"\"\"
  nickName: String
  \"\"\"
  This is a description of barkVolumn field
  \"\"\"
  barkVolumn: Int
}

\"\"\"
This is a description of the AddDogInput
\"\"\"
input AddDogInput {
  \"\"\"
  This is a description name
  \"\"\"
  name: String
  \"\"\"
  This is a description nickName
  \"\"\"
  nickName: String
  \"\"\"
  This is a description barkVolumn
  \"\"\"
  barkVolumn: Int
}

\"\"\"
This is a description of the top level mutations
\"\"\"
type Mutation {
  \"\"\"
  This is a description of the addDog mutation
  \"\"\"
  addDog(inputs: AddDogInput): AddDocResult
}
")

(t/deftest test-mutation-types
  (let [parsed-subject (gqlc.main/parse-&-transform-string mutation-type-examples)]

    (t/is (match? nil
                  (gqlc.main/all-to-datalog parsed-subject)))))

#_(t/deftest test-field-def
  (t/testing "Given the AST of a field def"
    (let [field-def-ast '(:fieldDef
                          (:anyName (:nameTokens "pets"))
                          ":"
                          (:typeSpec
                           (:listType
                            "["
                            (:typeSpec
                             (:typeName (:anyName (:nameTokens "Pet")))
                             (:required "!"))
                            "]")))]

      (t/is (match? nil?
                    (gqlc.main/ast->clj field-def-ast))))))

#_(t/deftest test-type-spec
  (t/testing "Given: the AST of typeSpec"
    (let [type-spec '(:typeSpec
                      (:typeName (:anyName (:nameTokens "Pet")))
                      (:required "!"))
          subject (gqlc.main/ast->clj type-spec)]

      (t/testing "When: ask to transform from AST to ClojureTypes"
        (t/is (match?
               nil?
               subject))))))


(t/deftest test-example-graphql-schema
  (let [graphql (load-grapql-example "test-data/example.graphql")]

    (t/is (match? nil graphql))))


(def type-def
  '(:typeDef
    ^#:clj-antlr{:position {:row 18, :column 0, :index 272, :stop 303}}
    (:description "\"\"\"\nA dog implements the pet\n\"\"\"")
    "type"
    ^#:clj-antlr{:position {:row 21, :column 5, :index 310, :stop 312}}
    (:anyName
     ^#:clj-antlr{:position {:row 21, :column 5, :index 310, :stop 312}}
     (:nameTokens "Dog"))
    ^#:clj-antlr{:position {:row 21, :column 9, :index 314, :stop 338}}
    (:implementationDef "implements" "Pet" "&" "NickName")
    ^#:clj-antlr{:position {:row 21, :column 35, :index 340, :stop 513}}
    (:fieldDefs
     "{"
     ^#:clj-antlr{:position {:row 22, :column 2, :index 344, :stop 356}}
     (:fieldDef
      ^#:clj-antlr{:position {:row 22, :column 2, :index 344, :stop 347}}
      (:anyName
       ^#:clj-antlr{:position
                    {:row 22, :column 2, :index 344, :stop 347}}
       (:nameTokens "name"))
      ":"
      ^#:clj-antlr{:position {:row 22, :column 8, :index 350, :stop 356}}
      (:typeSpec
       ^#:clj-antlr{:position
                    {:row 22, :column 8, :index 350, :stop 355}}
       (:typeName
        ^#:clj-antlr{:position
                     {:row 22, :column 8, :index 350, :stop 355}}
        (:anyName
         ^#:clj-antlr{:position
                      {:row 22, :column 8, :index 350, :stop 355}}
         (:nameTokens "String")))
       ^#:clj-antlr{:position
                    {:row 22, :column 14, :index 356, :stop 356}}
       (:required "!")))
     ^#:clj-antlr{:position {:row 23, :column 2, :index 360, :stop 375}}
     (:fieldDef
      ^#:clj-antlr{:position {:row 23, :column 2, :index 360, :stop 367}}
      (:anyName
       ^#:clj-antlr{:position
                    {:row 23, :column 2, :index 360, :stop 367}}
       (:nameTokens "nickname"))
      ":"
      ^#:clj-antlr{:position
                   {:row 23, :column 12, :index 370, :stop 375}}
      (:typeSpec
       ^#:clj-antlr{:position
                    {:row 23, :column 12, :index 370, :stop 375}}
       (:typeName
        ^#:clj-antlr{:position
                     {:row 23, :column 12, :index 370, :stop 375}}
        (:anyName
         ^#:clj-antlr{:position
                      {:row 23, :column 12, :index 370, :stop 375}}
         (:nameTokens "String")))))
     ^#:clj-antlr{:position {:row 24, :column 2, :index 379, :stop 393}}
     (:fieldDef
      ^#:clj-antlr{:position {:row 24, :column 2, :index 379, :stop 388}}
      (:anyName
       ^#:clj-antlr{:position
                    {:row 24, :column 2, :index 379, :stop 388}}
       (:nameTokens "barkVolume"))
      ":"
      ^#:clj-antlr{:position
                   {:row 24, :column 14, :index 391, :stop 393}}
      (:typeSpec
       ^#:clj-antlr{:position
                    {:row 24, :column 14, :index 391, :stop 393}}
       (:typeName
        ^#:clj-antlr{:position
                     {:row 24, :column 14, :index 391, :stop 393}}
        (:anyName
         ^#:clj-antlr{:position
                      {:row 24, :column 14, :index 391, :stop 393}}
         (:nameTokens "Int")))))
     ^#:clj-antlr{:position {:row 25, :column 2, :index 397, :stop 446}}
     (:fieldDef
      ^#:clj-antlr{:position {:row 25, :column 2, :index 397, :stop 411}}
      (:anyName
       ^#:clj-antlr{:position
                    {:row 25, :column 2, :index 397, :stop 411}}
       (:nameTokens "doesKnowCommand"))
      ^#:clj-antlr{:position
                   {:row 25, :column 17, :index 412, :stop 436}}
      (:argList
       "("
       ^#:clj-antlr{:position
                    {:row 25, :column 18, :index 413, :stop 435}}
       (:argument
        ^#:clj-antlr{:position
                     {:row 25, :column 18, :index 413, :stop 422}}
        (:anyName
         ^#:clj-antlr{:position
                      {:row 25, :column 18, :index 413, :stop 422}}
         (:nameTokens "dogCommand"))
        ":"
        ^#:clj-antlr{:position
                     {:row 25, :column 30, :index 425, :stop 435}}
        (:typeSpec
         ^#:clj-antlr{:position
                      {:row 25, :column 30, :index 425, :stop 434}}
         (:typeName
          ^#:clj-antlr{:position
                       {:row 25, :column 30, :index 425, :stop 434}}
          (:anyName
           ^#:clj-antlr{:position
                        {:row 25, :column 30, :index 425, :stop 434}}
           (:nameTokens "DogCommand")))
         ^#:clj-antlr{:position
                      {:row 25, :column 40, :index 435, :stop 435}}
         (:required "!")))
       ")")
      ":"
      ^#:clj-antlr{:position
                   {:row 25, :column 44, :index 439, :stop 446}}
      (:typeSpec
       ^#:clj-antlr{:position
                    {:row 25, :column 44, :index 439, :stop 445}}
       (:typeName
        ^#:clj-antlr{:position
                     {:row 25, :column 44, :index 439, :stop 445}}
        (:anyName
         ^#:clj-antlr{:position
                      {:row 25, :column 44, :index 439, :stop 445}}
         (:nameTokens "Boolean")))
       ^#:clj-antlr{:position
                    {:row 25, :column 51, :index 446, :stop 446}}
       (:required "!")))
     ^#:clj-antlr{:position {:row 26, :column 2, :index 450, :stop 496}}
     (:fieldDef
      ^#:clj-antlr{:position {:row 26, :column 2, :index 450, :stop 463}}
      (:anyName
       ^#:clj-antlr{:position
                    {:row 26, :column 2, :index 450, :stop 463}}
       (:nameTokens "isHouseTrained"))
      ^#:clj-antlr{:position
                   {:row 26, :column 16, :index 464, :stop 486}}
      (:argList
       "("
       ^#:clj-antlr{:position
                    {:row 26, :column 17, :index 465, :stop 485}}
       (:argument
        ^#:clj-antlr{:position
                     {:row 26, :column 17, :index 465, :stop 476}}
        (:anyName
         ^#:clj-antlr{:position
                      {:row 26, :column 17, :index 465, :stop 476}}
         (:nameTokens "atOtherHomes"))
        ":"
        ^#:clj-antlr{:position
                     {:row 26, :column 31, :index 479, :stop 485}}
        (:typeSpec
         ^#:clj-antlr{:position
                      {:row 26, :column 31, :index 479, :stop 485}}
         (:typeName
          ^#:clj-antlr{:position
                       {:row 26, :column 31, :index 479, :stop 485}}
          (:anyName
           ^#:clj-antlr{:position
                        {:row 26, :column 31, :index 479, :stop 485}}
           (:nameTokens "Boolean")))))
       ")")
      ":"
      ^#:clj-antlr{:position
                   {:row 26, :column 41, :index 489, :stop 496}}
      (:typeSpec
       ^#:clj-antlr{:position
                    {:row 26, :column 41, :index 489, :stop 495}}
       (:typeName
        ^#:clj-antlr{:position
                     {:row 26, :column 41, :index 489, :stop 495}}
        (:anyName
         ^#:clj-antlr{:position
                      {:row 26, :column 41, :index 489, :stop 495}}
         (:nameTokens "Boolean")))
       ^#:clj-antlr{:position
                    {:row 26, :column 48, :index 496, :stop 496}}
       (:required "!")))
     ^#:clj-antlr{:position {:row 27, :column 2, :index 500, :stop 511}}
     (:fieldDef
      ^#:clj-antlr{:position {:row 27, :column 2, :index 500, :stop 504}}
      (:anyName
       ^#:clj-antlr{:position
                    {:row 27, :column 2, :index 500, :stop 504}}
       (:nameTokens "owner"))
      ":"
      ^#:clj-antlr{:position {:row 27, :column 9, :index 507, :stop 511}}
      (:typeSpec
       ^#:clj-antlr{:position
                    {:row 27, :column 9, :index 507, :stop 511}}
       (:typeName
        ^#:clj-antlr{:position
                     {:row 27, :column 9, :index 507, :stop 511}}
        (:anyName


         ))))
     "}")))

#_(t/deftest test-type-spec-with-description
  (t/testing "Given: the AST of typeSpec"
    (let [subject (gqlc.main/ast->clj type-def)]

      (prn subject)

      (t/testing "When: ask to transform from AST to ClojureTypes"
        (t/is (match?
               nil?
               subject))))))
