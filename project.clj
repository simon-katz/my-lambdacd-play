(defproject my-lambdacd-play "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[com.taoensso/timbre "4.10.0"]
                 [lambdacd "0.13.5"]
                 [lambdacd-git "0.4.0"]
                 [lambdaui "1.0.0"]
                 [http-kit "2.2.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [ch.qos.logback/logback-core "1.0.13"]
                 [ch.qos.logback/logback-classic "1.0.13"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[midje "1.8.3"
                                   :exclusions [org.clojure/clojure]]
                                  [org.clojure/tools.namespace "0.2.11"]]
                   :plugins [[lein-midje "3.2.1"]]}
             :uberjar {:aot :all}}
  :main my-lambdacd-play.core
  :repl-options {:init-ns user})
