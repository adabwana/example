(ns visualize-data
  (:require
    [aerial.hanami.templates :as ht]
    [generate-data :refer [data]]
    [nextjournal.clerk :as clerk]
    [scicloj.clay.v2.api :as clay]
    [scicloj.kind-clerk.api :as kind-clerk]
    [scicloj.ml.dataset :as ds]
    [scicloj.noj.v1.vis.hanami :as hanami]))

(kind-clerk/setup!)

;; # Visualize Data
(comment
  (clerk/serve! {:browse? true :watch-paths ["."]})
  (clerk/show! "src/assignment/generate_data.clj")
  (clerk/build! {:paths ["notebooks/visualize_data.clj"]}))

(defn dist-range [dist]
  (-> (apply max dist)
      (-
        (apply min dist))))

(-> (ds/select-rows data #(= (:group %) "normal"))
    (hanami/histogram :x2 {:nbins 20}))

(-> (:x2 (ds/select-rows data #(= (:group %) "normal")))
    dist-range)

(-> (ds/select-rows data #(= (:group %) "gamma"))
    (hanami/histogram :x2 {:nbins 20}))

(-> (:x2 (ds/select-rows data #(= (:group %) "gamma")))
    dist-range)

(-> (ds/select-rows data #(= (:group %) "log-normal"))
    (hanami/histogram :x2 {:nbins 20}))

(-> (:x2 (ds/select-rows data #(= (:group %) "log-normal")))
    dist-range)

;; Scatter plot
(-> data
    (hanami/plot ht/point-chart
                 {:X "x1" :Y "x2" :COLOR "group"}))

(comment
  (clay/make! {:format      [:quarto :html]
               :source-path "notebooks/visualize_data.clj"
               :quarto      {:highlight-style :nord}}))