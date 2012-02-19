(ns rssminer.handlers.proxy
  (:use (rssminer [http :only [client]]
                  [util :only [assoc-if]]
                  [config :only [rssminer-conf]]))
  (:import rssminer.async.ProxyFuture
           org.jboss.netty.handler.codec.http.HttpResponse))

(defn- compute-send-header [req]
  (let [headers (:headers req)]
    (assoc-if {"X-Forwarded-For" (:remote-addr req)}
              "User-Agent" (headers "user-agent")
              "If-Modified-Since" (headers "if-modified-since")
              "Cache-Control" (headers "cache-control"))))

(defn handle-proxy [req]
  (let [uri (-> req :params :u)
        headers (compute-send-header req)]
    {:status 200
     ;; understand by async-ring-handler
     :body (ProxyFuture. client uri headers (:proxy @rssminer-conf)
                         (fn [{:keys [^HttpResponse resp final-link]}]
                           (.setHeader resp "Cache-Control"
                                       "public, max-age=604800")
                           {:status 200 :body resp}))}))

;;; buggy
;;; http://www.moandroid.com/?p=2020
