(defproject cardtooltip "0.1.0-SNAPSHOT"
  :description "cardtooltip"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :jar-name     "cardtooltip.jar"
  :uberjar-name "cardtooltip-standalone.jar"
  
  :main         cardtooltip.system
  
  :dependencies [[org.clojure/clojure "1.9.0-beta4"]
               [org.clojure/clojurescript "1.9.946"]
               [org.clojure/core.async  "0.3.443"]
              ; Web server
               [http-kit "2.2.0"]
               [com.stuartsierra/component "0.3.2"]
              ; routing
               [compojure "1.6.0"]
               [ring/ring-defaults "0.3.1"]
               [clj-http "3.7.0"]
              ; page rendering
               [hiccup "1.0.5"]
               [reagent "0.7.0"]
			   [funcool/octet "1.1.2"]]

  :plugins [[lein-figwheel "0.5.14"]
           [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
           [lein-autoexpect "1.9.0"]]

  :source-paths ["src"]

  :cljsbuild {:builds []}

  :figwheel { :css-dirs ["resources/public/css"]}

  ;; Setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles {:uberjar {:aot :all
                       :source-paths ["src"]}
                       ;; :prep-tasks ["compile" ["cljsbuild" "once" "min"]]}
            :dev {:dependencies [[reloaded.repl "0.2.4"]
                               [expectations "2.2.0-rc3"]
                               [binaryage/devtools "0.9.4"]
                               [figwheel-sidecar "0.5.14"]
                               [com.cemerick/piggieback "0.2.2"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
