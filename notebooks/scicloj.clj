(ns scicloj
  (:require
    [calc-metric.patch]
    [fastmath.stats :as stats]
    [nextjournal.clerk :as clerk]
    [scicloj.ml.core :as ml]
    [scicloj.ml.dataset :as ds]
    [scicloj.ml.metamorph :as mm]))

;; Run clerk functions in comment to evaluate namespace in browser interactively
(comment
  (clerk/serve! {:browse? true :watch-paths ["."]})
  (clerk/show! "notebooks/scicloj.clj"))

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
    (mm/std-scale regressors {})                            ;if :all, how unscale response?
    (mm/set-inference-target response)))

(defn elastic-net-pipe-fn [params]
  (ml/pipeline
    pipeline-fn
    {:metamorph/id :model} (mm/model (merge {:model-type :smile.regression/elastic-net}
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
(def elastic-pipelines
  (->> (ml/sobol-gridsearch
         (dissoc
           (ml/hyperparameters :smile.regression/elastic-net)
           :tolerance :max-iterations))                     ;only want lambda1 and lambda2
       (take 500)
       (map elastic-net-pipe-fn)))

(def eval-enet-val
  (ml/evaluate-pipelines
    elastic-pipelines
    train-val-splits
    stats/omega-sq
    :accuracy
    {:other-metrices                   [{:name :mae :metric-fn ml/mae}
                                        {:name :rmse :metric-fn ml/rmse}]
     :return-best-pipeline-only        false
     :return-best-crossvalidation-only true}))


;; ### Extract models
(defn best-models [eval]
  (->> eval
       flatten
       (map
         #(hash-map :summary (ml/thaw-model (get-in % [:fit-ctx :model]))
                    :fit-ctx (:fit-ctx %)
                    :timing-fit (:timing-fit %)
                    :metric ((comp :metric :test-transform) %)
                    :other-metrices ((comp :other-metrices :test-transform) %)
                    :params ((comp :options :model :fit-ctx) %)
                    :pipe-fn (:pipe-fn %)))
       (sort-by :mean)))

(def models-enet-val
  (best-models eval-enet-val))

;; ### Best model summary
^{::clerk/viewer clerk/code}
(-> models-enet-val first :summary)
