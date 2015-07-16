(ns picture-gallery.models.schema
  (:require [clojure.java.jdbc :as sql]
            [environ.core :refer [env]]
            [heroku-database-url-to-jdbc.core :as h]))

(def database-url (h/jdbc-connection-string (env :database-url)))

(def migratus-cfg {:store :database
                   :migration-dir "migrations"
                   :db database-url})

(def db {:connection-uri database-url})
