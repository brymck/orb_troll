(def sim-count 100000)

;; orb type helpers
(defn orb-to-int [orb]
  ({:fire 0 :water 1 :wood 2 :light 3 :dark 4 :heart 5} orb))

(defmacro get-orb [board orb]
  `(nth ~board ~(orb-to-int orb)))

(defn fire [board]
  (get-orb board :fire))

(defn water [board]
  (get-orb board :water))

(defn wood [board]
  (get-orb board :wood))

(defn light [board]
  (get-orb board :light))

(defn dark [board]
  (get-orb board :dark))

(defn heart [board]
  (get-orb board :heart))

(defn orbs [board]
  (take 5 board))

(defn swap-incs [board from to]
  (let [cnt (nth board from)]
    (update-in (update-in [0 0 0 0 0 0] [from] - cnt) [to] + cnt)))

(defn replace-indices [board froms tos]
  (vec (apply map + board (map #(swap-incs board %1 %2) froms tos))))

(defmacro replace-orbs [board orbs]
  `(replace-indices ~board ~@(->> orbs
                                 (map #(map orb-to-int %))
                                 (apply map list)
                                 (map vec))))

;; Match helpers
(defn match? [count]
  (>= count 3))

(defn no-match? [count]
  (< count 3))

(defn add-to-least [xs n]
  (update-in (vec (sort xs)) [0] + n))

(defn threes [count]
  (quot count 3))

(defn count-threes [board]
  (reduce + (map threes board)))

(defn at-least-threes? [n board]
  (>= (count-threes board) n))

(defn count-matches [board]
  (count (filter match? board)))

(defn at-least-matches? [n board]
  (>= (count-matches board) n))

(defn gen-board []
  (let [h (frequencies (repeatedly 30 #(rand-int 6)))]
    (mapv #(or (h %) 0) (range 6))))

(gen-board)

(defn simulate [leader & subs]
  (let [pred (reduce #(%2 %1) leader subs)]
    (loop [i 0 ts 0]
      (if (< i sim-count)
        (recur (inc i) (if (pred (gen-board)) (inc ts) ts))
        (float (/ ts i))))))

(defn translate-orbs [orbs]
  (case orbs
    :all [0 1 2 3 4 5]
    :colors [0 1 2 3 4]
    (mapv orb-to-int orbs)))

;; character types
(defmacro defrainbow
  ([name orbs]
     `(defn ~name [board#]
        (->> ~(translate-orbs orbs)
            (mapv board#)
            (every? match?))))
  ([name orbs cnt]
     `(defn ~name [board#]
        (->> ~(translate-orbs orbs)
             (mapv board#)
             (at-least-matches? ~cnt)))))

(defmacro defchanger [name orbs]
  `(defn ~name [f#]
     (fn [board#]
       (or (f# board#)
           (f# (replace-orbs board# ~orbs))))))

(defmacro defherogod [name orb n]
  `(defn ~name [board#]
     (let [cnt# (~orb board#)]
       (and (not (nil? cnt#))
            (>= cnt# ~n)))))

;; multipliers
(defn robin [board]
  (at-least-threes? 8 board))

(defn anubis [board]
  (at-least-threes? 10 board))

(defrainbow isis :colors 3)

(defrainbow kirin [:fire :water :wood :light])

(defrainbow suzaku [:fire :water :wood])

(defrainbow umiyama [:water :wood :light :dark])

(defrainbow horus :colors 4)

(defrainbow ra :colors)

(defrainbow plsdra :all 5)

(defherogod pandora dark 6)

(defchanger gigas     {:heart :fire})
(defchanger siegfried {:heart :water})
(defchanger cuchu     {:heart :wood})
(defchanger valk      {:heart :light})
(defchanger vampire   {:heart :dark})

(defchanger umiyama*  {:fire :water, :heart :wood})

(defn plsdra7 [board]
  (every? match? board))

(defn mastering [f]
  (fn [board]
    (or (f board)
        (f (gen-board)))))
