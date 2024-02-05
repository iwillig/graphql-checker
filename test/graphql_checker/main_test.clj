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
