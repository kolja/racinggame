(ns racinggame.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def track [[0.5 0.0 0.5]
            [0.6 0.2 0.4]
            [0.4 0.4 0.5]
            [0.5 0.6 0.3]
            [0.4 0.8 0.4]
            [0.7 1.0 0.5]
            [0.5 1.2 0.5]
            [0.6 1.4 0.4]
            [0.4 1.6 0.5]
            [0.5 1.8 0.3]
            [0.4 2.0 0.4]
            [0.7 2.2 0.5]])

(defn setup []
  (q/frame-rate 70)
  (q/color-mode :hsb)
  {:track track
   :car {:x-offset 0.5
         :speed {:x 0
                 :y -0.005}}
   :camera {:y-offset (second (last track))}})

(defn update-state [state]
  (-> state
      (update-in [:camera :y-offset] + (-> state :car :speed :y))
      (update-in [:car :x-offset] + (-> state :car :speed :x))))

(defn segment->points [[x y width]]
  {:left [(- x (/ width 2)) y]
   :right [(+ x (/ width 2)) y]})

(defn logical->screen [screen-width point]
  (mapv #(* screen-width %) point))

(comment
  (logical->screen 500 [0.6 0.7])
  (partition 2 1 [1 2 3])
  )
(defn key-pressed [state event]
  (case (:key event) 
    :left (assoc-in state [:car :speed :x] -0.005)
    :right (assoc-in state [:car :speed :x] 0.005)
    state))

(defn draw-state [state]
  (q/background 240)
  (let [track (:track state)
        points (map segment->points track)
        points-clockwise (concat (map :right points)
                                 (reverse (map :left points)))
        screen-width (q/width)
        screen-points (map #(logical->screen screen-width %) points-clockwise)
        polygon (partition 2 1 screen-points)]

    (doseq [[p1 p2] polygon]
       (q/line p1 p2)))
  (q/rect (-> state :car :x-offset
              (* (q/width)))
          (-> state :camera :y-offset
              (* (q/width)))
          20 20))


(q/defsketch racinggame
  :title "You spin my circle right round"
  :size [200 800]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :key-pressed key-pressed
  :middleware [m/fun-mode])
