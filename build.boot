(def project 'clj-jumanpp)
(def version "0.2.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.9.0-alpha15"]

                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [org.clojure/test.check "0.9.0" :scope "test"]
                            [adzerk/bootlaces "0.1.13" :scope "test"]

                            [org.clojure/data.csv "0.1.3"]
                            [me.raynes/conch "0.8.0"]])

(task-options!
 pom {:project     project
      :version     version
      :description "A Clojure library for idiomatic access to the Japanese Morphological Analyzer JUMAN++"
      :url         "https://github.com/borh/clj-jumanpp"
      :scm         {:url "https://github.com/borh/clj-jumanpp"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(require '[adzerk.bootlaces :refer :all])

(bootlaces! version)

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(deftask dev
  []
  (comp (watch) (build) (repl :init-ns 'clj-jumanpp.core :server true)))

(require '[adzerk.boot-test :refer [test]])
