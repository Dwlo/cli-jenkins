(ns clj-jenkins.rest-client
  (:require [cheshire.core          :as cc]
            [clj-http.client        :as rest-client]
            [clj-jenkins.properties :as pp]
            [clojure.walk           :as wlk]
            [clojure.string         :as str]))

(def auth-type "?os_authType=basic")

(defn get-body
  "Returns a decoded json body from a given json response"
  [response]
  (->> (rest-client/json-decode (:body response))
       wlk/keywordize-keys))

(defn rest-get
  "GET request on the Jenkins REST api."
  [uri]
  (let [address (if (str/starts-with? uri "http")
                  uri
                  (str pp/server-url uri))
        resp    (rest-client/get address
                                 {:basic-auth [pp/user-name pp/user-passwd]
                                  :accept :json})
        status (:status resp)]
    (if (= 200 status)
      (get-body resp)
      (throw (Exception. resp)))))

(defn rest-post
  "Post request on the Jenkins REST resource"
  [uri]
  (let [address (if (str/starts-with? uri "http")
                  uri
                  (str pp/server-url uri))
        resp    (rest-client/post address
                                  {:basic-auth [pp/user-name pp/user-passwd]
                                   :accept :json})
        status (:status resp)]
    (if (contains? [400 401 403 404] status)
      (throw (Exception. resp))
      (get-body resp))))
