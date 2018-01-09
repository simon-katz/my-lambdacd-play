(ns my-lambdacd-play.pipeline
  (:require [lambdacd.steps.control-flow :as cf]
            [lambdacd.steps.manualtrigger :as manualtrigger]
            [my-lambdacd-play.steps :as steps]))

(def pipeline-def
  ;; jsk-note: The backquote is needed. With quote, the symbols don't get
  ;; resolved. Nasty.
  `(manualtrigger/wait-for-manual-trigger
    steps/some-step-that-does-nothing
    (cf/in-parallel
     steps/some-step-that-echos-foo
     steps/some-step-that-echos-bar)
    manualtrigger/wait-for-manual-trigger
    steps/some-failing-step))
