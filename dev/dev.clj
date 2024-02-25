(ns dev
  (:require
   [kaocha.repl :as k]
   [clojure.tools.namespace.repl
    :as repl :refer [refresh]]))





(comment

  ;; Re compile clojure
  (refresh)

  ;; Run the main test
  (k/run 'graphql-checker.main-test)



  )
