(ns my-lambdacd-play.pipeline
  (:require [lambdacd.steps.control-flow :as cf]
            [lambdacd.steps.manualtrigger :as manualtrigger]
            [my-lambdacd-play.steps :as steps]))

;;;; jsk-note: The backquote is needed. With quote, the symbols don't get
;;;; resolved. Nasty.

(def pipeline-def
  (case 2
    1 `(manualtrigger/wait-for-manual-trigger
        steps/some-step-that-does-nothing
        (cf/in-parallel
         steps/some-step-that-echos-foo
         steps/some-step-that-echos-bar)
        manualtrigger/wait-for-manual-trigger
        steps/some-failing-step)
    2 `((cf/either
         manualtrigger/wait-for-manual-trigger
         steps/wait-for-repo)
        (cf/with-workspace
          steps/clone
          ;; manualtrigger/wait-for-manual-trigger
          steps/run-tests
          ;; manualtrigger/wait-for-manual-trigger
          ))))
