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
            [noir.util.anti-forgery :refer [anti-forgery-field]]
            [noir.util.route :refer [restricted]]
            [picture-gallery.models.image :as image]
            [picture-gallery.util :refer :all])
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

(defn scale [img ratio width height]
  (let [scale
        (AffineTransform/getScaleInstance
          (double ratio) (double ratio))
        transform-op (AffineTransformOp.
                       scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(def thumb-size 150)

(defn scale-image [file]
  (let [img
        (ImageIO/read file)
        img-width (.getWidth img)
        img-height (.getHeight img)
        ratio
        (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
      (scale-image (io/input-stream (str path filename)))
      "png"
      (File. (str path thumb-prefix filename)))))

(defn handle-upload [{:keys [filename] :as file}]
  (upload-page
    (if (empty? filename)
      "please select a file to upload"
      (try
        (noir.io/upload-file (gallery-path) file :create-path? true)
        (save-thumbnail file)
        (image/create (session/get :user) filename)
        (image {:height "150px"}
               (thumb-uri (session/get :user) filename))
        (catch Exception ex
          (str "error uploading file " (.getMessage ex)))))))

(defn serve-file [user-id file-name]
  (file-response (str galleries File/separator user-id File/separator file-name)))

(defn delete-image [userid name]
  (try
    (image/delete userid name)
    (io/delete-file (str (gallery-path) File/separator name))
    (io/delete-file (str (gallery-path) File/separator thumb-prefix name))
    "ok"
    (catch Exception ex (.getMessage ex))))

(defn delete-images [names]
  (let [userid (session/get :user)]
    (resp/json
      (for [name names] {:name name :status (delete-image userid name)}))))

(defroutes upload-routes
  (GET "/upload" [info] (restricted (upload-page info)))
  (POST "/upload" [file] (restricted (handle-upload file)))
  (GET "/img/:user-id/:file-name" [user-id file-name] (serve-file user-id file-name))
  (POST "/delete" [names] (restricted (delete-images names))))
