(ns simple-search.experiment
  (:require [simple-search.core-hillclimbing :as core])
  (:use simple-search.knapsack-examples.knapPI_11_20_1000
        simple-search.knapsack-examples.knapPI_13_20_1000
        simple-search.knapsack-examples.knapPI_16_20_1000
        simple-search.knapsack-examples.knapPI_11_200_1000
        simple-search.knapsack-examples.knapPI_13_200_1000
        simple-search.knapsack-examples.knapPI_16_200_1000
        simple-search.knapsack-examples.knapPI_11_1000_1000
        simple-search.knapsack-examples.knapPI_13_1000_1000
        simple-search.knapsack-examples.knapPI_16_1000_1000))

(defn run-experiment
  [searchers problems num-replications max-evals]
  (println "Search_method Problem Max_evals Run Score")
  (for [searcher searchers
        p problems
        n (range num-replications)]
    (let [answer  (searcher p max-evals)]
      {:searcher searcher
       :problem p
       :max-evals max-evals
       :run-number n
       :answer answer})))

(defn print-experimental-results
  [results]
  (doseq [result results]
    (println (:label (meta (:searcher result)))
             (:label (:problem result))
             (:max-evals result)
             (:run-number result)
             (:score (:answer result)))))

;; This really shouldn't be necessary, as I should have included the labels
;; in the maps when generated the problem files. Unfortunately I only just
;; realized that, and it's easier to do this than have everyone merge in a
;; new set of problem files to all your projects.
(defn get-labelled-problem
  "Takes the name of a problem (as a string) and returns the actual
   problem instance (as a map) with the name added to the map under
   the :label key."
  [problem-name]
  (let [problem (var-get (resolve (symbol problem-name)))]
    (assoc problem :label problem-name)))

(defn -main
  "Runs a set of experiments with the number of repetitions and maximum
  answers (tries) specified on the command line.

  To run this use something like:

  lein run -m simple-search.experiment 30 1000

  where you replace 30 and 1000 with the desired number of repetitions
  and maximum answers.
  "
  [num-repetitions max-answers]
  ; This is necessary to "move" us into this namespace. Otherwise we'll
  ; be in the "user" namespace, and the references to the problems won't
  ; resolve propertly.
  (ns simple-search.experiment)
  (print-experimental-results
    (run-experiment [(with-meta
                      (partial core/hill-search core/tweaker)
                      {:label "HC_T"})
                    (with-meta
                      (partial core/hill-search core/tweaker-with-rates)
                      {:label "HC_TR"})
                    (with-meta
                      (partial core/hill-search-with-random-restart core/tweaker)
                      {:label "HCR_T"})
                    (with-meta
                      (partial core/hill-search-with-random-restart core/tweaker-with-rates)
                      {:label "HCR_TR"})
                    (with-meta (partial core/random-search)
                      {:label "RS"})]
                   (map get-labelled-problem
;;                         ["knapPI_11_20_1000_4" "knapPI_13_20_1000_4" "knapPI_16_20_1000_4"
;;                          "knapPI_11_200_1000_4" "knapPI_13_200_1000_4" "knapPI_16_200_1000_4"])
                         ["knapPI_11_1000_1000_4" "knapPI_13_1000_1000_4" "knapPI_16_1000_1000_4"])
                   (Integer/parseInt num-repetitions)
                   (Integer/parseInt max-answers)))
  (shutdown-agents))
