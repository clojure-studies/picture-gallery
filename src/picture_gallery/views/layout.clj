(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [noir.util.anti-forgery :refer [anti-forgery-field]]))

(defn base [& content]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body content]))

(defn common [& content]
  (base
    (if-let [user (session/get :user)]
      [:div (link-to "/logout" (str "logout " user))]
      [:div (link-to "/register" "register")
       (form-to [:post "/login"]
                (anti-forgery-field)
                (text-field {:placeholder "screen name"} "id")
                (password-field {:placeholder "password"} "pass")
                (submit-button "login"))])
    content))
