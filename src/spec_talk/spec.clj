(ns spec-talk.spec
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]))

;; left pad specs ---------------

(s/def ::string string?)

(defn- padding-gen []
  (-> (s/gen (s/coll-of char? :min-count 1 :max-count 3))
      (gen/bind #(gen/return (apply str %)))))

(s/def ::padding (s/with-gen (s/and string?
                                    #(<= 1 (count %) 3))
                             padding-gen))
(s/def ::amount (s/and int?
                       #(< 0 %)))

(s/def ::left-pad-handler-params
  (s/keys :req-un [::string ::padding ::amount]))



;; Ring handler specs -----------

(s/def ::non-empty-string
  (s/and string? #(> (count %) 0)))

(s/def ::params-base
  (s/map-of keyword? ::non-empty-string))

(defn- params-gen []
  (gen/one-of [(->> (s/gen ::left-pad-handler-params)
                    (gen/fmap #(update % :amount str)))
               (s/gen ::params-base)]))

(s/def ::params
  (s/with-gen ::params-base
              params-gen))

(s/def ::ring-request
  (s/keys :req-un [::params]))

(s/def ::status #{200 400})

(s/def ::ring-response
  (s/keys :req-un [::status ::headers]))

(s/def ::padded-string string?)

(s/def ::valid-body
  (s/keys :req-un [::padded-string]))

(s/def ::invalid-body #{{:error "Bad request"}})

(s/def ::body
  (s/or :valid-request ::valid-body
        :invalid-request ::invalid-body))

(s/def ::headers
  (s/map-of ::non-empty-string ::non-empty-string))

(defn- left-pad-ring-response-pred
  [{:keys [status body]}]
  (let [[body-type _] body]
    (case status
      400 (= :invalid-request body-type)
      200 (= :valid-request body-type))))

(s/def ::left-pad-ring-response
  (s/and (s/keys :req-un [::status ::headers ::body])
         left-pad-ring-response-pred))
