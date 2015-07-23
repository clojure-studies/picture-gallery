(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [noir.util.anti-forgery :refer [anti-forgery-field]]))

(defn base [& content]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
     (include-js "//code.jquery.com/jquery-2.0.2.min.js")
    [:body content]))

(defn make-menu [& items]
  [:div (for [item items] [:div.menuitem item])])

(defn guest-menu []
  (make-menu
    (link-to "/" "home")
    (link-to "/register" "register")
    (form-to [:post "/login"]
             (anti-forgery-field)
             (text-field {:placeholder "screen name"} "id")
             (password-field {:placeholder "password"} "pass")
             (submit-button "login"))))

(defn user-menu [user]
  (make-menu
    (link-to "/upload" "upload images")
    (link-to "/logout" (str "logout " user))))

(defn common [& content]
  (base
        (if-let [user (session/get :user)]
          (user-menu user)
          (guest-menu))
        [:div.content content]))
