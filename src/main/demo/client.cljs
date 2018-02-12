(ns demo.client
  (:require [fulcro.client :as fc]
            [demo.ui.root :as root]))

(defonce app (atom nil))

(defn mount []
  (reset! app (fc/mount @app root/Root "app")))

(defn start []
  (mount))

(defn ^:export init []
  (reset! app (fc/new-fulcro-client))
  (start))
