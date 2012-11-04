(ns rssminer.handlers.reader
  (:use (rssminer [util :only [user-id-from-session md5-sum ignore-error
                               to-int serialize-to-js]]
                  [search :only [search*]])
        [clojure.java.io :only [resource]]
        [ring.util.response :only [redirect]]
        [rssminer.db.user :only [find-user-by-email find-user-by-id]])
  (:require [rssminer.config :as cfg]
            [clojure.string :as str]
            [rssminer.db.feed :as db]
            [rssminer.tmpls :as tmpls])
  (:import rssminer.Utils
           rssminer.FaviconFuture))

(def landing-css (ignore-error (slurp "public/css/landing.css")))
(def app-css (ignore-error (slurp "public/css/app.css")))

(defn show-unsupported-page [req]
  (tmpls/browser {:css landing-css}))

(defn show-landing-page [req]
  (if (= (-> req :params :r) "d")       ; redirect to /demo
    (redirect "/demo")
    (if (cfg/real-user? req)
      (redirect "/a")
      (let [body (tmpls/landing { :css landing-css})]
        (if (cfg/demo-user? req) {:status 200
                                  :session nil ;; delete cookie
                                  :session-cookie-attrs {:max-age -1}
                                  :body body}
            body)))))

(defn show-app-page [req]
  (if (cfg/demo-user? req)
    (assoc (redirect "/") :session nil ;; delete cookie
           :session-cookie-attrs {:max-age -1})
    (when-let [user (find-user-by-id (user-id-from-session req))]
      (tmpls/app {:css app-css
                  :email (:email user)
                  :md5 (-> user :email md5-sum)
                  :data (serialize-to-js
                         {:rm {:user user
                               :gw (-> req :params :gw) ; google import wait
                               :ge (-> req :params :ge) ; google import error
                               :static_server (:static-server @cfg/rssminer-conf)}})}))))

(defn show-demo-page [req]
  (if (cfg/real-user? req)
    (assoc (redirect "/?r=d") :session nil ;; delete cookie
           :session-cookie-attrs {:max-age -1})
    (do
      (swap! cfg/rssminer-conf assoc :demo-user ;in case score updated
             (dissoc (find-user-by-email "demo@rssminer.net")
                     :password))
      (let [user (:demo-user @cfg/rssminer-conf)
            data {:rm {:user user
                       :demo true
                       :static_server (:static-server @cfg/rssminer-conf)}}]
        {:body (tmpls/app {:email (:email user)
                           :md5 (-> user :email md5-sum)
                           :css app-css
                           :data (serialize-to-js data)})
         :status 200
         :session (:demo-user @cfg/rssminer-conf)}))))

(defn search [req]
  (let [{:keys [q limit tags authors fs offset]} (:params req)
        uid (user-id-from-session req)
        limit (min 20 (to-int limit))
        offset (min (to-int (or offset "0")) 80)
        fs (= fs "1")]
    (search* q tags authors uid limit offset fs)))

(defn get-favicon [req]
  (if-let [hostname (-> req :params :h str/reverse)]
    {:status 200
     :body (FaviconFuture. hostname
                           {"User-Agent" ((:headers req) "user-agent")}
                           @cfg/rssminer-conf)}
    {:status 404}))
