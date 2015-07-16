(ns picture-gallery.models.user
  (:require [clojure.java.jdbc :as sql])
  (:use [picture-gallery.models.schema]))

(defn create [user]
  (sql/insert! db :users user))

(defn all []
  (sql/query db ["SELECT * FROM users"]))

(defn find-first [name]
  (sql/query db ["SELECT * FROM users WHERE name=? LIMIT 1" name] :result-set-fn first))

(defn destroy [where-clause]
  (sql/delete! db :users where-clause))
