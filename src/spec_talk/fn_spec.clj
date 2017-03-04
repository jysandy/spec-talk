(ns spec-talk.fn-spec
  (:require [clojure.spec :as s]
            [spec-talk.spec :as core-spec]
            [spec-talk.core :as core]
            [clojure.string :as clj-string]))

(comment
  (s/fdef core/left-pad-handler
          ;; Arguments are specified as a sequence.
          ;; s/cat specifies a sequence of items.
          ;; Each item in the sequence must be named.
          :args (s/cat :request ::core-spec/left-pad-ring-request)

          ;; The return value spec
          :ret ::core-spec/left-pad-ring-response))
























































(comment
  ;; The fn spec receives a map with args and ret.
  (defn- left-pad-handler-pred
    [{:keys [args ret]}]
    ;; All values are conformed. So we can check the body type.
    (let [[body-type body] (:body ret)]
      (or (= body-type :invalid-request)
          (let [{:keys [string-to-pad padding]} (get-in args [:request :params])
                returned-string (:padded-string body)]
            (or (= string returned-string)
                (and (clj-string/starts-with? returned-string padding)
                     (clj-string/ends-with? returned-string string)))))))

  (s/fdef core/left-pad-handler
          :args (s/cat :request ::core-spec/left-pad-ring-request)
          :ret ::core-spec/left-pad-ring-response
          ;; The fn spec can be used to specify a relationship
          ;; between the arguments and the return value.
          :fn left-pad-handler-pred))
