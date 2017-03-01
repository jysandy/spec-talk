(ns spec-talk.core
  (:require [org.httpkit.server :as http-kit]
            [ring.util.response :as res]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [bidi.ring :refer [make-handler]]
            [clojure.spec :as s]
            [spec-talk.spec :as core-spec])
  (:gen-class))

(defn left-pad
  [string padding amount]
  (str (apply str (repeat amount padding))
       string))

(def bad-request (-> (res/response {:error "Bad request"})
                     (res/status 400)))

(defn- conform-or-nil [spec value]
  (when (s/valid? spec value)
    (s/conform spec value)))

(defn left-pad-handler
  [{:keys [params] :as request}]
  (try
    (if-let [{:keys [string padding amount]} (conform-or-nil ::core-spec/left-pad-handler-params
                                                             (update params :amount #(Integer/parseInt %)))]
      (res/response {:padded-string (left-pad string padding amount)})
      bad-request)
    (catch java.lang.NumberFormatException e
      bad-request)))

(def app-handler
  (make-handler ["/" {"left-pad" left-pad-handler}]))

(defonce server-stop-fn (atom nil))

(defn start-server! []
  (reset! server-stop-fn (http-kit/run-server
                           (-> app-handler
                               wrap-keyword-params
                               wrap-params
                               wrap-json-response)
                           {:port 8080})))

(defn stop-server! []
  (when-let [stop-fn @server-stop-fn]
    (stop-fn)
    (reset! server-stop-fn nil)))

(defn restart-server! []
  (stop-server!)
  (start-server!))

(defn -main
  [& args]
  (println "Starting server...")
  (start-server!)
  (println "Server started."))
