(ns clj-jenkins.build
  (:require [clj-time.coerce :as tcoerce]))

(defrecord Build [id number url display-name duration estimated-duration executor result started-at upstream-build-number upstream-job upstream-build-url])

(defn get-id [json]
  (:id json))

(defn get-number [json]
  (:number json))

(defn get-url [json]
  (str (:url json) "api/json"))

(defn get-display-name [json]
  (:displayName json))

(defn get-duration [json]
  (:duration json))

(defn get-estimated-duration [json]
  (:estimatedDuration json))

(defn get-executor [json]
  (:executor json))

(defn get-result [json]
  (:result json))

(defn get-timestamp [json]
  (tcoerce/from-long (:timestamp json)))

(defn get-upstream-build-number [json]
  (->> (:actions json)
       (filter #(= "hudson.model.CauseAction" (:_class %)))
       first
       :causes
       (filter #(= "hudson.model.Cause$UpstreamCause" (:_class %)))
       first
       :upstreamBuild))

(defn get-upstream-job [json]
  (->> (:actions json)
       (filter #(= "hudson.model.CauseAction" (:_class %)))
       first
       :causes
       (filter #(= "hudson.model.Cause$UpstreamCause" (:_class %)))
       first
       :upstreamProject))

(defn get-upstream-build-url [json]
  (->> (:actions json)
       (filter #(= "hudson.model.CauseAction" (:_class %)))
       first
       :causes
       (filter #(= "hudson.model.Cause$UpstreamCause" (:_class %)))
       first
       :upstreamUrl))

(def select-values (juxt get-id
                         get-number
                         get-url
                         get-display-name
                         get-duration
                         get-estimated-duration
                         get-executor
                         get-result
                         get-timestamp
                         get-upstream-build-number
                         get-upstream-job
                         get-upstream-build-url))

(defn parse [json]
  (->> json
       (select-values)
       (apply ->Build)))
