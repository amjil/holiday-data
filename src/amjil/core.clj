(ns amjil.core
  (:require [clj-time.core :as time]
            [clj-time.format :as time-format]
            [clj-http.client :as http]
            [mount.core :as mount]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]
            [amjil.config :refer [env]]
            [amjil.db :as db])
  (:gen-class))

(defn date-is-holiday
  [day]
  (let [res (http/get (str "http://api.goseek.cn/Tools/holiday?date=" day))]
    (if (= 200 (:status res))
      [day (-> res :body json/read-str (get "data"))])))

(defn date-list-for-year
  [year]
  (let [start-date (time/date-time year)
        end-date (time/date-time (+ 1 year))]
    (->> (take-while #(time/before? % end-date) (map #(time/plus start-date (time/days %)) (range)))
         (map #(time-format/unparse (time-format/formatter "yyyyMMdd") %)))))

(defn log1 [] (log/warn "aaa"))

(defn settle-days-of-year
  [year]
  (let [days (date-list-for-year year)]
    (db/delete-all year)
    (log/warn "Deleted " year " of data.")
    (->> (map #(date-is-holiday %) days)
         (map #(list (->> (nth % 0) (time-format/parse (time-format/formatter "yyyyMMdd")) (time-format/unparse (time-format/formatter "yyyy-MM-dd"))) (nth % 1)))
         (db/insert-data))
    (log/warn "Inserted " year " of data.")))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(def year-options
  [["-y" "--year YEAR" "YEAR"
    :parse-fn #(Integer/parseInt %)]])

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts year-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args)
  (let [year (-> (parse-opts args year-options)
                 :options
                 :year)]
    (settle-days-of-year year)))
