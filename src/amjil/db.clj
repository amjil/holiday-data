(ns amjil.db
  (:require [mount.core :as mount]
            [amjil.config :refer [env]]
            [clojure.java.jdbc :as jdbc]))


(mount/defstate ^{:on-reload :noop} td
  :start
  {:datasource (doto (com.teradata.jdbc.TeraDataSource.)
                    (.setDatabaseName "NGBASS")
                    (.setUser (:td-username env))
                    (.setpassword (:td-password env))
                    (.setDSName (:td-server env))
                    (.setCLIENT_CHARSET "cp936")
                    (.setDbsPort (:td-port env)))})

;
(extend-protocol jdbc/IResultSetReadColumn java.sql.Date (result-set-read-column [col _ _] (str col)))


(defn delete-all [year]
  ; (jdbc/execute! td ["delete from NGBASS.IOP_calendar_holiday where extract(year from cal_date) = ?" year])
  (jdbc/delete! td :iop_calendar_holiday ["extract(year from cal_date) = ?" year]))

(defn insert-data [data]
  (jdbc/insert-multi! td :iop_calendar_holiday nil data))

(defn query-all [year]
  (jdbc/query td ["select * from ngbass.iop_calendar_holiday where extract(year from cal_date) = ?" year]))
