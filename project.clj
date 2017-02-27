(defproject spec-talk "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/test.check "0.9.0"]
                 [bidi "2.0.16"]
                 [http-kit "2.2.0"]
                 [ring "1.5.0"]
                 [ring/ring-json "0.4.0"]]
  :main ^:skip-aot spec-talk.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :monkeypatch-clojure-test false)
