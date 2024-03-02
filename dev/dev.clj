(ns dev
  (:require
   [kaocha.repl :as k]
   [clj-reload.core :as reload]))

(reload/init
  {:dirs ["src" "dev" "test"]})


(comment

  (reload/reload)
  ;; Re compile clojure

  ;; Run the main test
  (k/run 'graphql-checker.main-test)



  )
