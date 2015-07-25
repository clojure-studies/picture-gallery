(ns picture-gallery.test.handler
  (:require [noir.util.crypt :refer [encrypt]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]])
  (:use clojure.test
        ring.mock.request
        picture-gallery.handler))

(defn mock-find-first-user [id]
  (if (= id "foo")
    {:id "foo" :pass (encrypt "12345")}))

(deftest test-login
  (testing "login success"
    (with-redefs [picture-gallery.models.user/find-first mock-find-first-user]
      (is
        (-> (request :post "/login" {:id "foo" :pass "12345" :__anti-forgery-token *anti-forgery-token*})
            app :headers (get "Set-Cookie") not-empty))))
  (testing "password mismatch"
    (with-redefs [picture-gallery.models.user/find-first mock-find-first-user]
      (is
        (-> (request :post "/login" {:id "foo" :pass "123456"})
            app :headers (get "Set-Cookie") empty?))))
  (testing "user not found"
    (with-redefs [picture-gallery.models.user/find-first mock-find-first-user]
      (is
        (-> (request :post "/login" {:id "bar" :pass "12345"})
            app :headers (get "Set-Cookie") empty?)))))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (.contains (:body response) "home"))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))
