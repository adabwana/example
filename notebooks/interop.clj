(ns interop
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

(require-python '[numpy :as np])
(require-python '[pandas :as pd])
(require-python 'itertools
                '(itertools product))
(require-python 'sklearn.model_selection
                '(sklearn.model_selection train_test_split GridSearchCV))
(require-python 'sklearn.linear_model
                '(sklearn.linear_model ElasticNet))
(require-python 'sklearn.metrics
                '(sklearn.metrics mean_absolute_error mean_squared_error r2_score))
(require-python 'time
                '(time time))

;; Run clerk functions in comment to evaluate namespace in browser interactively
(comment
  (clerk/serve! {:browse? true :watch-paths ["."]})
  (clerk/show! "notebooks/interop.clj"))

;; ### Load data
(defonce autompg
         (ds/dataset "data/autompg.csv"
                     {:key-fn keyword}))

(comment
  "not sure where to go from here if i want to pure interop. help would be nice or direction to docs on doing machine learning with the libpython-clj2.python work flow. but priority is in sklearn, if only because process is too similar to regular scicloj smile.")