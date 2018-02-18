(ns clj-jenkins.task
  (:require [clj-time.coerce :as tcoerce]))


(defrecord Task [project-name project-url color since why])

;; json represents an item

(defn get-project-name [json]
  (get-in json [:task :name]))

(defn get-project-url [json]
  (:url json))

(defn get-color [json]
  (get-in json [:task :color]))

(defn get-since [json]
  (tcoerce/from-long (:inQueueSince json)))

(defn get-why [json]
  (:why json))

(def select-values (juxt get-project-name
                         get-project-url
                         get-color
                         get-since
                         get-why))

(defn parse [json]
  (->> json
       (select-values)
       (apply ->Task)))
