(ns wfpm-clj-code.glue.lazy-evaluation.integration
  (:require [wfpm-clj-code.glue.lazy-evaluation.differentiation :as d]))

;; easyintegrate f a b= (f a+f b)∗(b−a)/2

(defn easy-integrate [f a b]
  (/ (* (+ (f a) (f b)) (- b a)) 2))

; example function and values used in this namespace
; f(x) = 3x² + 5
; see: https://www.wolframalpha.com/input/?i=integral+f%28x%29+%3D+3%2Ax%C2%B2+%2B5+from+1+to+2
(def fx #(+ (* 3 % %) 5))
(def a 1)
(def b 2)

(easy-integrate fx a b)
;=> 25/2 = 12.5, right result would be 12

(defn mid [a b] (/ (+ a b) 2))

(defn integrate-1st [f a b]
  (lazy-seq (cons (easy-integrate f a b)
                  (map +
                       (integrate-1st f a (mid a b))
                       (integrate-1st f (mid a b) b)))))

(take 4 (integrate-1st fx a b))
; => (25/2 97/8 385/32 1537/128)

;; lazy-seq of approximations, the 4th result, 1537/128 = 12.0078, is already quite accurate

;; note: zip2 (Cons a s) (Cons b t) =Cons(a, b) (zip2s t)
;; explicit 'zip' is not necessary in clojure, because map can handle multiple collections which then will be zipped together.
;; zip example in clojure:
(map vector [1 3 5] [2 4 6])
; => ([1 2] [3 4] [5 6])

; inner- recursive-function used in 'integrate-2d', produces a lazy-seq.
(defn integ [f a b fa fb]
  (lazy-seq (cons (/ (* (+ fa fb) (- b a)) 2)
                  (map +
                       (integ f a         (mid a b) fa            (f (mid a b)))
                       (integ f (mid a b) b         (f (mid a b)) fb)))))

(defn integrate-2nd [f a b]
  (integ f a b (f a) (f b)))

; without 'take' it would run forever or crash a some point!
(take 4 (integrate-2nd fx a b))
;=> (25/2 97/8 385/32 1537/128)
; same result but with less recalculations => better performance

; the performance optimized integrate function combined with the function from the previous section
; see 'within', 'relative' and 'improve' from 'differentiation' namespace
(let [eps 0.001]
  (str "within: " (d/within eps (integrate-2nd fx a b))
       " relative: " (d/relative eps (integrate-2nd fx a b))
       " improve: " (first (d/improve (integrate-2nd fx a b)))))
;=> "within: 98305/8192 relative: 6145/512 improve: 12"
