(ns clojure-art.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def size 500)
(def step (/ size 20))

(def colors '("#D40920" "#1356A2" "#F7D842"))

(defn split-on-x [square split-at]
  (list {:x (:x square)
         :y (:y square)
         :width (- (:width square) (- (:width square) (+ split-at (:x square))))
         :height (:height square)}
        {:x split-at
         :y (:y square)
         :width (- (:width square) (+ split-at (:x square)))
         :height (:height square)}))

(defn split-on-y [square split-at]
  (list {:x (:x square)
         :y (:y square)
         :width (:width square)
         :height (- (:width square) (- (:width square) (+ split-at (:y square))))}
        {:x (:x square)
         :y split-at
         :width (:width square)
         :height (- (:height square) (+ split-at (:y square)))}))

(defn split-squares-with [coordinates squares]
  (let [x (:x coordinates)
        y (:y coordinates)
        res (reduce (fn [acc square]
                      (concat
                       (cond
                 ;; split on x: return the two squares split into on x axis
                         (and x (< x (+ (:x square) (:width square)))) (split-on-x square x)
                 ;; split on y: return the two squares split into on the y axis
                         (and y (< y (+ (:y square) (:height square)))) (split-on-y square y)
                 ;; otherwise, do not split - keep the original square
                         :else (list square))
                       acc))
                    '() squares)]
    (println res)
    res))

(def initial-squares (list {:x 0 :y 0 :width size :height size}))
(defn setup []
  (q/frame-rate 1)
  ;; initial state
  {:color 1
   :step 0
   :squares initial-squares})

(defn draw-state [state]
  ;; set stroke weight of 8
  (q/stroke-weight 8)
  ;; Color the background white
  (q/background 255)
  ;; draw all of the calculated rectangles
  (doseq [rc (:squares state)]
    (q/fill (if (:color rc) (:color rc) "#F2F5F1"))
    (q/rect (:x rc) (:y rc) (:width rc) (:height rc))))

(defn update-state [state]
  (let
   [step (+ step (:step state))
    split-obj (if (> (.random js/Math) 0.5) {:x step} {:y step})]
    {:color (+ 1 (:color state))
     :step (if (= (count (:squares state)) 0) 0 step)
     :squares (cond
                ;; once we've filled the grid, we start decreasing the squares
                (>= step size) (rest (:squares state))
                ;; but when we have no squares left, we reset to the initial square
                (= (count (:squares state)) 0) initial-squares
                ;; otherwise, split!
                :else (split-squares-with split-obj (:squares state)))}))

(defn ^:export run-sketch []
  (q/defsketch clojure-art
    :host "first-sketch"
    :size [2000 2000]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

                                        ; uncomment this line to reset the sketch:
(run-sketch)
