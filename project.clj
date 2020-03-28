(defproject wfpm-clj-code "0.1.0-SNAPSHOT"
  :description "Code examples from 'Why Functional Programming Matters' in clojure
   and some thought, notes and links to other references"
  :url "https://github.com/jbartelh/wfpm-clj-code"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :key "mit"
            :year 2020}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :main ^:skip-aot wfpm-clj-code.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
