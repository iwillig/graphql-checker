(ns graphql-checker.main-test
  (:require
   [graphql-checker.main :as gqlc.main]
   [matcher-combinators.test]
   ;;[matcher-combinators.matchers :as m]
   [clojure.test :as t]))

(t/deftest test-okay
  (t/is
   (match?
    {:key :value
     :other-key :another-value}
    {:other-key :another-value
     :key :value})))

(t/deftest test-parse-schema
  (t/testing "when we parse the schema"
    (let [thing (gqlc.main/-main)]
      (binding [*print-meta* true]
        (t/is (match? nil?
                      (gqlc.main/xform thing)))))))

(t/deftest test-name-tokens
  (t/testing "Given: A name-token-ast"
    (t/testing "When: We transform it into Clojure data"
      (t/testing "Then: We except it to be a "))))

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
             (gqlc.main/xform
              subject))))))


(t/deftest test-field-def
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
                    (gqlc.main/xform field-def-ast))))))

(t/deftest test-type-spec
  (t/testing "Given: the AST of typeSpec"
    (let [type-spec '(:typeSpec
                      (:typeName (:anyName (:nameTokens "Pet")))
                      (:required "!"))
          subject (gqlc.main/xform type-spec)]

      (t/testing "When: ask to transform from AST to ClojureTypes"
        (t/is (match?
               nil?
               subject))))))

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
         ^#:clj-antlr{:position
                      {:row 27, :column 9, :index 507, :stop 511}}
         (:nameTokens "Human")))))
     "}")))

(t/deftest test-type-spec-with-description
  (t/testing "Given: the AST of typeSpec"
    (let [subject (gqlc.main/xform type-def)]

      (prn subject)

      (t/testing "When: ask to transform from AST to ClojureTypes"
        (t/is (match?
               nil?
               subject))))))
