## Help Requested
There were a few issues I've encountered with this project.

1) `notebooks/visualize_data.clj` Clay is not showing data in plots whereas, clerk will with the `clerk/vl` viewer.
2) `notebooks/lda.clj` `ml/evaluate-pipeline` will not run. Error :message reads "invalid type." I tried removing one of the three response categories in hopes I could get a binary algorithm running and evaluating. Eventually, I'd still like to evalaute the pipeline with three response categories and evaulate with `ml/classification-accuracy`, `stats/cohens-kappa` and/or f1 (is f1 applicable with response of three categories?).

Thank you in advance.
