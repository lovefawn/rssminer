(ns freader.test-common
  (:require [freader.config])
  (:use [freader.routes :only [app]]
        [clojure.java.io :only [resource]]
        [sandbar.stateful-session :only [session-get]]))

(def test-user {:name "feng"
                :password "123456"
                :email "shenedu@gmail.com"})

(def test-rss-str
  (slurp (resource "test-rss.xml")))

(defn mock-http-get [& args]
  {:body test-rss-str})

(def test-app
  (app))

(def auth-app
  (let [mock-session-get (fn [arg]
                           (if (=  arg :user)
                             (assoc test-user
                               :id 1)
                             arg))]
    (fn [& args]
      (binding [session-get mock-session-get]
        (apply (app) args)))))
