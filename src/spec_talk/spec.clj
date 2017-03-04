(ns spec-talk.spec
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]))

;; left pad specs ---------------

;; specs are defined by composing predicates.
(s/def ::string-to-pad string?)

;; Compose predicates/specs using s/and
(s/def ::amount (s/and int?
                       #(< 0 %)))

(s/def ::padding (s/and string?
                        #(<= 1 (count %) 3)))

;; Specify a set of keys using s/keys
(s/def ::left-pad-handler-params
  (s/keys :req-un [::string-to-pad ::padding ::amount]))




















































(comment
  (defn- padding-gen []
    ;; s/coll-of specifies a collection of the given spec
    ;; s/gen returns the generator of a spec
    (->> (s/gen (s/coll-of char? :min-count 1 :max-count 3))
         ;;(gen/fmap f g) accepts a generator `g` and returns a generator
         ;; which applies `f` to each of the values generated by `g`.
         (gen/fmap #(apply str %))))

  (s/def ::padding (s/with-gen (s/and string?
                                      #(<= 1 (count %) 3))
                               padding-gen)))


























;; Ring handler specs -----------

(comment
  (s/def ::non-empty-string
    (s/and string? #(> (count %) 0)))

  ;; Specify a map of homogeneous keys to homogeneous values with s/map-of
  ;; Not to be confused with s/keys
  (s/def ::params
    (s/map-of keyword? ::non-empty-string))

  (s/def ::left-pad-ring-request
    (s/keys :req-un [::params]))

  ;; Sets can be used like predicates in Clojure!
  ;; So a set is a valid spec.
  (s/def ::status #{200 400})

  (s/def ::headers
    (s/map-of ::non-empty-string ::non-empty-string))

  (s/def ::padded-string string?)

  ;; A valid request body contains the padded string.
  (s/def ::valid-body
    (s/keys :req-un [::padded-string]))

  ;; An invalid request body is simply this map.
  (s/def ::invalid-body #{{:error "Bad request"}})

  ;; Combine multiple possible specs with s/or.
  ;; Note the name which precedes each spec.
  ;; Whenever predicate checking involves a branch,
  ;; spec requires that you name each branch.
  ;; This is useful when conforming and for error reporting.
  (s/def ::body
    (s/or :valid-request ::valid-body
          :invalid-request ::invalid-body))

  (s/def ::left-pad-ring-response
    (s/keys :req-un [::status ::headers ::body])))


































































(comment
  (defn- left-pad-ring-response-pred
    [{:keys [status body]}]
    ;; s/and passes the conformed value to successive predicates.
    ;; So we can destructure the body and check its type.
    (let [[body-type _] body]
      (case status
        400 (= :invalid-request body-type)
        200 (= :valid-request body-type))))

  (s/def ::left-pad-ring-response
    (s/and (s/keys :req-un [::status ::headers ::body])
           left-pad-ring-response-pred)))









































































(comment
  ;; The same spec as before.
  (s/def ::params-base
    (s/map-of keyword? ::non-empty-string))

  (defn- params-gen []
    ;; Params values must always be strings.
    ;; So generate the valid params, then stringify the amount.
    (gen/one-of [(->> (s/gen ::left-pad-handler-params)
                      (gen/fmap #(update % :amount str)))
                 (s/gen ::params-base)]))

  (s/def ::params
    (s/with-gen ::params-base
                params-gen)))
