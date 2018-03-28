(ns clj-jenkins.job)

(defrecord Job [name display-name url color health-report upstream-projects downstream-projects
                concurrent-build buildable next-build-number last-build last-completed-build last-failed-build
                last-stable-build last-unstable-build last-successful-build last-unsuccessful-build
                builds])

(defn get-name [json]
  (:name json))

(defn get-display-name [json]
  (:displayName json))

(defn get-url [json]
  (:url json))

(defn get-color [json]
  (:color json))

(defn get-health-report [json]
  (:healthReport json))

(defn get-upstream-projects [json]
  (:upstreamProjects json))

(defn get-downstream-projects [json]
  (:downstreamProjects json))

(defn is-concurrent-build [json]
  (:concurrentBuild json))

(defn is-buildable [json]
  (:buildable json))

(defn get-next-build-number [json]
  (:nextBuildNumber json))

(defn get-last-build-number [json]
  (get-in json [:lastBuild :number]))

(defn get-last-completed-build-number [json]
  (get-in json [:lastCompletedBuild :number]))

(defn get-last-failed-build-number [json]
  (get-in json [:lastFailedBuild :number]))

(defn get-last-stable-build [json]
  (get-in json [:lastStableBuild :number]))

(defn get-last-unstable-build [json]
  (get-in json [:lastUnstableBuild :number]))

(defn get-last-successful-build [json]
  (get-in json [:lastSuccessfulBuild :number]))

(defn get-last-unsuccessful-build [json]
  (get-in json [:lastUnsuccessfulBuild :number]))

(defn get-builds [json]
  (->> (get-in json [:builds])
       (map :url)))


(def select-values (juxt get-name
                         get-display-name
                         get-url
                         get-color
                         get-health-report
                         get-upstream-projects
                         get-downstream-projects
                         is-concurrent-build
                         is-buildable
                         get-next-build-number
                         get-last-build-number
                         get-last-completed-build-number
                         get-last-failed-build-number
                         get-last-stable-build
                         get-last-unstable-build
                         get-last-successful-build
                         get-last-unsuccessful-build
                         get-builds))

(defn parse [json]
  (->> json
       (select-values)
       (apply ->Job)))
