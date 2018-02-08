(ns clj-jenkins.jenkins-client
  (:require [clj-jenkins.rest-client :as rest]))


(defn list-builds
  "Lists job builds"
  [job-name]
  (->> (rest/rest-get (str "/job/" job-name "/api/json"))
       :builds
       (map #(select-keys % [:number :url]))))
