(ns spec-talk.fn-spec
  (:require [clojure.spec :as s]
            [spec-talk.spec :as core-spec]
            [spec-talk.core :as core]
            [clojure.string :as clj-string]))

(defn- left-pad-handler-pred
  [{:keys [args ret]}]
  (let [[body-type body] (:body ret)]
    (or (= body-type :invalid-request)
        (let [{:keys [string padding]} (get-in args [:request :params])
              returned-string (:padded-string body)]
          (or (= string returned-string)
              (and (clj-string/starts-with? returned-string padding)
                   (clj-string/ends-with? returned-string string)))))))

(s/fdef core/left-pad-handler
        :args (s/cat :request ::core-spec/ring-request)
        :ret ::core-spec/left-pad-ring-response
        :fn left-pad-handler-pred)
