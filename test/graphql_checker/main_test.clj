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

      (binding [*print-meta* false]

        (t/is (match? nil?
                      thing))))))


(t/deftest test-xform

  (t/testing "When given a valid type def"
    (t/is (match?
           nil?
           (gqlc.main/xform
            '(:typeDef
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
               "}")))
           ))

    ))
