(ns my-lambdacd-play.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [lambdacd.core :as lambdacd]
            [lambdacd.runners :as runners]
            [lambdacd.util :as util]
            [my-lambdacd-play.pipeline :as pipeline]
            [my-lambdacd-play.ui-selection :as ui-selection]
            [org.httpkit.server :as http-kit]))

(defrecord MySystem [name
                     stop-server-fun
                     pipeline])

(defmethod clojure.core/print-method MySystem [x writer]
  (.write writer (:name x)))

(defn -main [& args]
  (let [;; the home dir is where LambdaCD saves all data.
        ;; point this to a particular directory to keep builds around after restarting
        home-dir (case 2
                   1 (util/create-temp-dir)
                   2 "/Users/simonkatz/__no-backup/lambacd-play/work-area")
        config   {:home-dir home-dir
                  :name     "my lambdacd play"}
        ;; initialize and wire everything together
        pipeline (lambdacd/assemble-pipeline pipeline/pipeline-def config)
        ;; create a Ring handler for the UI
        app      (ui-selection/ui-routes pipeline)]
    (log/info "LambdaCD Home Directory is " home-dir)
    ;; this starts the pipeline and runs one build after the other.
    ;; there are other runners and you can define your own as well.
    (runners/start-one-run-after-another pipeline)
    ;; start the webserver to serve the UI
    (map->MySystem
     {:name "My instance of MySystem"
      :stop-server-fun (http-kit/run-server app {:open-browser? false
                                                 :port          8080})
      :pipeline pipeline})))
