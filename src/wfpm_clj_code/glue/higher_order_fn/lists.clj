(ns wfpm-clj-code.glue.higher-order-fn.lists
  (:require [clojure.core.reducers :as r]))

;; 3  Gluing Functions Together

;; List definition in the paper: 'listof∗::=Nil|Cons∗(listof∗)'

#_(cons 1 nil)
;; => (1)

(def just-a-list (cons 1 (cons 2 (cons 3 nil))))
;; => (1,2,3)

(defn sum-1st [[n & list :as coll]]
  (if-not coll
    0
    (+ n (sum-1st list))))

(sum-1st [1 2 3])
; => 6

(defn foldr [f x [a & l :as coll]]
  (if-not coll
    x
    (f a (foldr f x l))))

;; modularized version of sum with foldr
(def sum (partial foldr + 0))
(sum [1 2 3])
; => 6

;; clojure.core.reducers also work, they work differently but here they calculate the same result
;; more about the differences: https://www.braveclojure.com/quests/reducers/know-your-reducers/
#_(reduce + 0 [1 2 3])
#_(r/fold + [1 2 3])

(def product (partial foldr * 1))
(product [1 2 3 4])
; => 24

;; 'or' and 'and' are macros not functions, therefore wrapping
(def anytrue (partial foldr #(or %1 %2) false))
(anytrue [true false true])                                 ; => true
(anytrue [false false false])                               ; => false

(def alltrue (partial foldr #(and %1 %2) true))
(alltrue [true true false])                                 ; => false
(alltrue [true true true])                                  ; => true

;; just copying the collection
(foldr cons nil [1 2 3])
; => (1 2 3)

(defn append [a b]
  (foldr cons b a))

(append [1,2] [3,4])
; => (1 2 3 4)

;; same as clojure's 'concat'
(concat [1 2] [3 4])
; => (1 2 3 4)

(defn my-count [a n] (+ n 1))
(def length (partial foldr my-count 0))
(length [1 2 3 4])
; => 4
;; same as clojure's 'count'
(count [1 2 3 4])
; => 4

(defn double-and-cons-1st [n list] (cons (* 2 n) list))
(def double-all-1st (partial foldr double-and-cons-1st nil))
(double-all-1st [1 2 3 4])
; => (2 4 6 8)

;; same as before just more modularization of double-and-cons
(def double-fn #(* 2 %))
;; (Cons.f)
(defn f-and-cons [f el list] (cons (f el) list))
;; (Cons.double)
(def double-and-cons-2nd (partial f-and-cons double-fn))
;; doubleall=foldr(Cons. double) Nil
(def double-all-2nd (partial foldr double-and-cons-2nd nil))
(double-all-2nd [1 2 3 4])
; => (2 4 6 8)

;; our own 'map' implementation based on 'foldr'
(defn my-map [f coll]
  (foldr #(cons (f %1) %2) nil coll))

(def double-all (partial my-map double-fn))
(double-all [1 2 3 4])
; => (2 4 6 8)
;; again, it's the same with clojure's own 'map' function
(map double-fn [1 2 3 4])
; => (2 4 6 8)

; (f.g)h => #(f (g h %))

;; summatrix=sum.map sum
(def matrix [[1 2 3] [3 4 5] [5 6 7]])
(def summatrix #(sum (map sum %)))
(summatrix matrix)




