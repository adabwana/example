(ns sklearn
  (:require
    [calc-metric.patch]                                     ; ns instead of ms]
    [fastmath.stats :as stats]
    [nextjournal.clerk :as clerk]
    [scicloj.ml.core :as ml]
    [scicloj.ml.dataset :as ds]
    [scicloj.ml.metamorph :as mm]
    [scicloj.sklearn-clj.metamorph :as sklearn-mm]
    [scicloj.sklearn-clj.ml]
    [libpython-clj2.require :refer [require-python]]
    [libpython-clj2.python :refer [py. py.. py.-] :as py]))

;; Run clerk functions in comment to evaluate namespace in browser interactively
(comment
  (clerk/serve! {:browse? true :watch-paths ["."]})
  (clerk/show! "notebooks/sklearn.clj"))

;; ### Load data
(defonce autompg
         (ds/dataset "data/autompg.csv"
                     {:key-fn keyword}))

;; ### Define regressor and response
(def response :mpg)
(def regressors
  (remove #(= response %) (ds/column-names autompg)))

;; ## Build pipelines
(def pipeline-fn
  (ml/pipeline
    (mm/std-scale regressors {})
    (mm/set-inference-target response)))

(defn sklearn-pipe-fn [params]
  (ml/pipeline
    pipeline-fn
    {:metamorph/id :model}
    (mm/model
      (merge {:model-type :sklearn.regression/elastic-net}
             params))))

;; ## Partition data
;; #### For final model building after selecting top hyperparameters for elastic net
(def ds-split                                               ;:split-names [:train-val :test]
  (ds/split->seq autompg :kfold {:seed 123 :k 5 :ratio [0.8 0.2]}))
;; #### For hyperparameter tuning
(def train-val-splits
  (ds/split->seq
    (:train (first ds-split))
    :kfold {:seed 123 :k 5}))

;; ## Build models
(def sklearn-pipelines
  (->>
    (ml/sobol-gridsearch {:alpha (ml/linear 0 1 500)})      ;doesnt like l1-ratio, why??
    (map sklearn-pipe-fn)))

(def evaluations-sklearn                                    ;the travesty
  (ml/evaluate-pipelines
    sklearn-pipelines
    train-val-splits
    stats/omega-sq
    :accuracy
    {:other-metrices                   [{:name :mae :metric-fn ml/mae}
                                        {:name :rmse :metric-fn ml/rmse}]
     :return-best-pipeline-only        false
     :return-best-crossvalidation-only false}))

(comment                                                    ; above wont work bc "AttributeError: 'ElasticNet' object has no attribute 'predict_proba'"
  (def models-ridge
    (->> evaluations-sklearn
         flatten
         (map
           #(hash-map :model (ml/thaw-model (get-in % [:fit-ctx :model]))
                      :metric ((comp :metric :test-transform) %)
                      :fit-ctx (:fit-ctx %)))
         (sort-by :mean))))
