(ns spec-talk.core
  (:require [org.httpkit.server :as http-kit]
            [ring.util.response :as res]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [bidi.ring :refer [make-handler]])
  (:gen-class))

(defn left-pad
  [string padding amount]
  (str (apply str (repeat amount padding))
       string))

(defn left-pad-handler
  [{:keys [params] :as request}]
  (try
    (let [{:keys [string padding amount]} params
          amount-int (Integer/parseInt amount)]
      (res/response {:padded-string (left-pad string padding amount-int)}))
    (catch java.lang.NumberFormatException e
      (-> (res/response {:error "Bad request"})
          (res/status 400)))))

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
