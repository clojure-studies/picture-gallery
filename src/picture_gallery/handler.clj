(ns picture-gallery.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [picture-gallery.routes.home :refer [home-routes]]
            [noir.util.middleware :as noir-middleware]
            [picture-gallery.models.schema :as schema]
            [migratus.core :as migratus]
            [picture-gallery.routes.auth :refer [auth-routes]]))

(defn init []
  (println "picture-gallery is starting"))
  (migratus/migrate schema/migratus-cfg)

(defn destroy []
  (println "picture-gallery is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler
           [auth-routes
            home-routes
            app-routes]))
