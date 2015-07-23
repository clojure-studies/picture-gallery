(ns picture-gallery.models.image
  (:require [clojure.java.jdbc :as sql])
  (:use [picture-gallery.models.schema]))

(def t-con (sql/get-connection db))

(defn create [userid name]
  (sql/with-db-transaction [t-con db]
    (if (sql/query t-con
          ["SELECT userid FROM images WHERE userid = ? AND name = ?" userid name]
          :result-set-fn empty?)
      (sql/insert! t-con :images {:userid userid :name name})
      (throw
             (Exception. "you have already uploaded an image with the same name")))))

(defn delete [userid name]
  (sql/delete! db :images ["userid=? AND name=?" userid name]))

(defn by-user [userid]
  (sql/query db ["SELECT * FROM images WHERE userid=?" userid]))

(defn get-gallery-previews []
  (sql/query db ["SELECT * FROM (SELECT *, row_number() OVER (PARTITION BY userid) AS row_number FROM images) AS rows WHERE row_number = 1"]))
