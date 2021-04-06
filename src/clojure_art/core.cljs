(ns clojure-art.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def size 1000)
(def step 80)
(defn fifty-fifty [] (> (.random js/Math) 0.5))

(defn fp-while [pred fun val]
  (if (pred val) val (fp-while pred fun (fun val))))

(defn get-steps [] (reverse
                    (fp-while
                    ;; stop when size is less than the first of the list
                     (fn [ls] (< size (first ls)))
                    ;; add the step size to the first of the list then cons result on
                     (fn [ls] (cons (+ (first ls) step) ls))
                    ;; start with 0
                     (list 0))))

(defn draw-line [x y step]
  (if (fifty-fifty)
    (q/line x y (+ x step) (+ y step))
    (q/line (+ x step) y x (+ y step))))

(defn draw-state [state]
  (q/stroke-weight 2)
  (doseq [i (get-steps)]
    (doseq [j (get-steps)]
      (draw-line i j step))))

(defn setup []
  (q/frame-rate 0.5)
  (println (get-steps))
  {})

(defn update-state [state] state)

(defn ^:export run-sketch []
  (q/defsketch clojure-art
    :host "first-sketch"
    :size [size size]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

;; (run-sketch)
