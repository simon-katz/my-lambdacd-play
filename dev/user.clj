(ns user
  (:require [clojure.java.javadoc :refer [javadoc]]
            [clojure.pprint :refer [pp pprint]]
            [clojure.repl :refer :all ; [apropos dir doc find-doc pst source]
             ]
            [clojure.string :as str]
            [clojure.tools.namespace.move :refer :all]
            [clojure.tools.namespace.repl :refer :all]
            ;; [com.stuartsierra.component]
            [midje.repl :refer :all]
            [my-lambdacd-play.core :as core]
            [taoensso.timbre :as timbre]))

;;;; ___________________________________________________________________________
;;;; Clojure workflow.
;;;; See:
;;;; - http://nomistech.blogspot.co.uk/2013/06/stuart-sierras-clojure-development_18.html
;;;; - http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded
;;;; - https://github.com/stuartsierra/component#reloading

(defonce the-system
  ;; "A container for the current instance of the application.
  ;; Only used for interactive development."
  ;; 
  ;; Don't want to lose this value if this file is recompiled (when
  ;; changes are made to the useful additional utilities for the REPL
  ;; at the end of the file), so use `defonce`.
  ;; But note that this /is/ blatted when a `reset` is done.
  nil)

(defn init
  "Creates a system and makes it the current development system."
  []
  (alter-var-root #'the-system
                  (fn [system] system)))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'the-system
                  (fn [the-system]
                    (timbre/info "Starting")
                    (core/-main))))

(defn stop-server [system]
  (if-not (and system
               (:stop-server-fun system))
    system
    (do
      (timbre/info "Stopping server")
      ((:stop-server-fun system))
      (dissoc system :stop-server-fun))))

(defn stop-pipeline [system]
  (if-not (and system
               (:pipeline system))
    system
    (do
      (timbre/info "Stopping pipeline")
      (let [ctx (-> system
                    :pipeline
                    :context)
            shutdown-sequence (-> ctx
                                  :config
                                  :shutdown-sequence)]
        (shutdown-sequence ctx))
      (dissoc system :pipeline))))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'the-system
                  (fn [system]
                    ;; FIXME There's stuff created by `core/-main` that isn't
                    ;;       getting shutdown (I think).
                    (timbre/info "Stopping system")
                    (-> system
                        stop-server
                        stop-pipeline))))

(defn go
  "Creates a system, makes it the current development system and starts it."
  []
  (init)
  (start))

(defn reset
  "Stop, refresh and go."
  []
  (stop)
  (refresh :after 'user/go))

(defn reset-all
  "Stop, refresh-all and go."
  []
  (stop)
  (refresh-all :after 'user/go))

;;;; ___________________________________________________________________________
;;;; ---- u-classpath ----

(defn u-classpath []
  (str/split (System/getProperty "java.class.path")
             #":"))

;;;; ___________________________________________________________________________
;;;; ---- u-move-ns-dev-src-test ----

(defn u-move-ns-dev-src-test [old-sym new-sym source-path]
  (move-ns old-sym new-sym source-path ["dev" "src" "test"]))

;;;; ___________________________________________________________________________
;;;; App-specific additional utilities for the REPL or command line
