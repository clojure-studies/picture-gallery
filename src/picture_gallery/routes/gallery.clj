(ns picture-gallery.routes.gallery
  (:require [compojure.core :refer :all] [hiccup.element :refer :all]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.util
             :refer [thumb-prefix image-uri thumb-uri]]
            [picture-gallery.models.image :as image]
            [noir.session :as session]
            [hiccup.form :refer [check-box]]))

(defn thumbnail-link [{:keys [userid name]}]
  [:div.thumbnail
    [:a {:href (image-uri userid name)}
     (image (thumb-uri userid name))
     (if (= userid (session/get :user)) (check-box name))]])

(defn display-gallery [userid]
  (if-let [gallery (not-empty (map thumbnail-link (image/by-user userid)))]
    [:div
     [:div#error]
     gallery
     (if (= userid (session/get :user))
       [:input#delete {:type "submit" :value "delete images"}])]
    [:p "The user " userid " does not have any galleries"]))

(defn gallery-link [{:keys [userid name]}]
  [:div.thumbnail
  [:a
    {:href (str "/gallery/" userid)}
      (image (thumb-uri userid name))
      userid "'s gallery"]])

(defn show-galleries []
  (map gallery-link (image/get-gallery-previews)))

(defroutes gallery-routes
  (GET "/gallery/:userid" [userid]
       (include-js "/js/gallery.js")
       (layout/common (display-gallery userid))))
