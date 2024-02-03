(ns graphql-checker.main-test
  (:require
   [matcher-combinators.test]
   ;;[matcher-combinators.matchers :as m]
   [clojure.test :as t]))

(t/deftest test-okay
  (t/is
   (match?
    false?
    true)))
