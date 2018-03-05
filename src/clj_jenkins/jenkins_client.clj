(ns clj-jenkins.jenkins-client
  (:require [clj-jenkins
             [build        :as build]
             [rest-client  :as rest]
             [task         :as tsk]
             [job          :as job]]
            [clj-time.core :as tcore]))

;; Administration related functions


;; Queue related functions

(defn- list-queued-items
  []
  (->> (rest/rest-get "queue/api/json")
       :items))

(defn queue-size
  "Returns the Jenkins queue size"
  []
  (->> (list-queued-items)
       count))

(defn list-queued-tasks
  "Lists queued tasks"
  []
  (->> (list-queued-items)
       (map #(tsk/parse %))))

(defn list-waiting-tasks-since
  [mins]
  (->> (list-queued-tasks)
       (filter #(tcore/before? (:since %) (-> mins tcore/minutes tcore/ago)))))

;; Project related functions

(defn list-projects
  "Lists all Jenkins projects"
  []
  (->> (rest/rest-get "/api/json?tree=jobs[name,url,color]&wrapper=jobs")
       :jobs
       (map (fn [prj] {:color (:color prj)
                      :name  (:name prj)
                      :url   (str (:url prj) "api/json")}))))

(defn list-running-projects
  "List all running projects"
  []
  (->> (list-projects)
       (filter #(and (not (nil? (:color %)))
                     (clojure.string/ends-with? (:color %) "_anime")))))

(defn get-project
  "Returns details related to a project"
  [url]
  (->> (rest/rest-get url)
       (job/parse)))

(defn list-disabled-projects
  "Returns all disabled projects"
  []
  (->> (list-projects)
       (pmap #(get-project (:url %)))
       (filter #(not (:buildable %)))))

(declare get-build)
(defn list-projects-not-run-since-days
  "Return the urls of projects not run since a given day"
  [days]
  (->> (list-projects)
       (pmap #(let [prj (get-project (:url %))]
                (->> (str (:url prj) (:last-build prj) "/api/json")
                     (get-build))))
       (filter #(tcore/before? (:started-at %) (-> days tcore/days tcore/ago)))
       (map #(clojure.string/replace (:url %)
                                     #"/[0-9]*/api/json$"
                                     "/api/json"))))

;; Build related functions

(defn list-running-builds
  "Returns a url link for all running builds"
  []
  (->> (rest/rest-get "computer/api/json?tree=computer[executors[currentExecutable[url]],oneOffExecutors[currentExecutable[url]]]")
                                 :computer
                                 (map #(:executors %))
                                 (apply concat)
                                 (filter #(not (nil? (:currentExecutable %))))
                                 (map #(str (get-in % [:currentExecutable :url]) "api/json"))))
(defn get-build
  "Retrieves details related to a build"
  ([url]
   (->> (rest/rest-get url)
        build/parse))
  ([project-name number]
   (->> (rest/rest-get (str "job/" project-name "/" number  "/api/json"))
        build/parse)))

(defn list-builds-running-more-then
  [mins]
  (->> (list-running-builds)
       (pmap #(get-build %))
       (filter #(tcore/before? (:started-at %) (-> mins tcore/minutes tcore/ago)))))
