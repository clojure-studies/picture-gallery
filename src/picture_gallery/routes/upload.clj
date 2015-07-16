(ns picture-gallery.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode]]
            [picture-gallery.views.layout :as layout]
            [noir.io :refer [upload-file]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [noir.util.anti-forgery :refer [anti-forgery-field]])
  (:import [java.io File FileInputStream FileOutputStream]
           [java.awt.image AffineTransformOp BufferedImage]
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO))

(defn upload-page [info]
  (layout/common
                 [:h2 "Upload an image"]
                 [:p info]
                 (form-to {:enctype "multipart/form-data"}
                          [:post "/upload"]
                          (anti-forgery-field)
                          (file-upload :file)
                          (submit-button "upload"))))

(defn gallery-path []
  "public/galleries")

(defn handle-upload [{:keys [filename] :as file}]
  (upload-page
    (if (empty? filename)
      "please select a file to upload"
      (try
        (noir.io/upload-file (gallery-path) file :create-path? true)
        (image {:height "150px"}
               (str "/img/" (url-encode filename)))
        (catch Exception ex
          (str "error uploading file " (.getMessage ex)))))))

(defn serve-file [file-name]
  (file-response (str (gallery-path) File/separator file-name)))

(defroutes upload-routes
  (GET "/upload" [info] (upload-page info))
  (POST "/upload" [file] (handle-upload file))
  (GET "/img/:file-name" [file-name] (serve-file file-name)))
