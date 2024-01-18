(a fork of a repo by adabwana)

## Help Requested
There were a few issues I've encountered with this project.

1) `notebooks/visualize_data.clj` Clay is not showing data in plots whereas, clerk will with the `clerk/vl` viewer.
2) `notebooks/lda.clj` `ml/evaluate-pipeline` will not run. Error :message reads "invalid type." I tried removing one of the three response categories in hopes I could get a binary algorithm running and evaluating. Eventually, I'd still like to evalaute the pipeline with three response categories and evaulate with `ml/classification-accuracy`, `stats/cohens-kappa` and/or f1 (is f1 applicable with response of three categories?).

Thank you in advance.

### Fork comments by daslu

This fork to adabwana's project adds the following:

Added [kind-clerk](https://github.com/scicloj/kind-clerk) so that [Kindly](https://scicloj.github.io/kindly-noted/kindly) visualizations work in Clerk.

In any namespace where this is needed has to explicitly call:
```clj
(require ''[scicloj.kind-clerk.api :as kind-clerk])
(kind-clerk/setup!)
```

Rendered [notebooks/visualize_data.clj](notebooks/visualize_data.clj):
- [in Clay](https://daslu.github.io/adabwana-example/docs/visualize_data)
- [in Clerk](https://daslu.github.io/adabwana-example/public/build/)
