(defproject holiday-data "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [org.clojure/clojure "1.9.0"]
                 [clj-time "0.14.4"]
                 [clj-http "3.9.1"]
                 [cprop "0.1.13"]
                 [mount "0.1.13"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [td/tdgs "1.0.0"]
                 [td/tdjdbc "1.0.0"]]

  :plugins [[lein-ancient "0.6.15"]]
  :jvm-opts ["-Dconf=dev-config.edn"]

  :main ^:skip-aot amjil.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
