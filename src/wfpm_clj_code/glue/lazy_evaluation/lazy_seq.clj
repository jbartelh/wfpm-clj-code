(ns wfpm-clj-code.glue.lazy-evaluation.lazy-seq
  (:require [clojure.math.numeric-tower :refer [abs expt round]]))

;; next nx = (x + n/x)/2
(defn next-sqrt [n x]
  (/ (+ x (/ n x)) 2))
(next-sqrt 9 1)

;; the clojure-syntax for the described 'repeat f a = Cons(repeat f (f a))' function
#_(defn repeat-sqrt [f a] (cons a (repeat-sqrt f (f a))))
;; since function calls in clojure aren't lazy, this crashed your REPL!
;; more about laziness in Clojure:
;; http://clojure-doc.org/articles/language/laziness.html#commonly-used-functions-that-produce-lazy-sequences


;; but clojure has lazy sequences and 'iterate' is clojure's version of 'repeat'
(defn newton-sqrt [n x] (iterate (partial next-sqrt n) x))

;; test: calculate the square-root of 9 with 1 as guess and get the first 5th approximations
(take 5 (newton-sqrt 9 1))


;; some other experiments i've tried to implement the 'repeat f a' function with 'lazy-seq'

;; not-working: lazy-seq doesn't seem to like carrying the function f around
#_(defn lazy-repeat [f a] (lazy-seq (cons a (lazy-repeat f (f a)))))

;; working: the static function f is declared outside as 'sqrt9'
#_(def sqrt9 (partial next-sqrt 9))
#_(defn lazy-repeat [f a] (lazy-seq (cons a (lazy-repeat (sqrt9 a)))))

;; not-working: tried to define a inner function binding the static function f and calling it afterwards
#_(defn lazy-repeat [f a]
  (let [rep (fn l-repeat [n] (lazy-seq (cons n (l-repeat (f n)))))]
    (rep a)))

;; not-working: switched arguments f a -> a f, maybe the lazy-seq-macros can handle it better, but nope.
#_(defn lazy-repeat [a f] (lazy-seq (cons a (lazy-repeat (f a) f))))

;; i've tried to get as close as possible to the 'repeat (next n) a0' notation described in the paper
;; with 'lazy-seq' in clojure
#_(take 5 (lazy-repeat 1 (partial next-sqrt 9)))

;; but iterate works fine with n=9 amd a0=1 (same as above but without function declaration)
(take 10 (iterate (partial next-sqrt 9) 1))


;; pull as long from the lazy-seq until the tolerance is small enough.
;; b (second) is not destructured here, because we had to add it back to the seq.
;; cons a value to a lazy-seq is possible and clojure doesn't throw any errors when implementing
;; it with destructuring b (second), but the REPL is crashing
;; improvement: might be possible to destructure with keeping the original to just avoid the 'cons'
(defn within [eps [a & rest]]
  (if (<= (abs (- a (first rest))) eps)
    (first rest)
    (within eps rest)))


(defn sqrt [a0 eps n]
  (within eps (iterate (partial next-sqrt n) a0)))

;; test: calculate the square root of 9, with an initial guess of 1 and a tolerance of 0.01
(sqrt 1 0.01 9)


(defn relative [eps [a & rest]]
      (if (<= (abs (- (/ a (first rest)) 1)) eps)
        (first rest)
        (relative eps rest)))

(defn relative-sqrt [a0 eps n]
  (relative eps (iterate (partial next-sqrt n) a0)))

(relative-sqrt 1 0.01 9)

;; problem: find a right approximation for h. idea: reuse 'within eps'
(defn easydiff [f x h]
  (/ (- (f (+ x h)) (f x)) h))

(def halve #(/ % 2))

(defn differentiate [h0 f x]
  (map #(easydiff f x %) (iterate halve h0)))

;; e.g: f(x) = y = 3x² + 5
;; now differentiate a x=1 with h0=2
(let [fx #(+ (* 3 % %) 5)
      eps 0.01
      h0 2
      x 1]
  (within eps (differentiate h0 fx x)))

;; optimize the approximation function
;; returns a lazy-seq
(defn elimerror [n [a & rest]]
  (lazy-seq (cons (/ (- (* (first rest) (expt 2 n)) a) (- (expt 2 n) 1)) (elimerror n rest))))

;; clojure? do i really have to define something like log2 by myself? and do i really have to use java.lang.Math?
(defn log2 [n]
  (/ (Math/log n) (Math/log 2)))

;; round(log2 ((a−c)/(b−c)−1))
;; returns a single val
(defn order [[a b c & rest]]
  (do
    (println "a: " a  " b:" b " c:"  c " a-c: " (- a c) " b-c: " (- b c))
    (round (log2 (-(/ (- a c) (- b c)) 1)))))

;; returns a lazy-seq
(defn improve [s]
  (elimerror (order s) s))

(let [fx #(+ (* 3 % %) 5)
      eps 1
      h0 100
      x 1]
  (within eps (improve (differentiate h0 fx x))))

(let [fx #(+ (* 3 % %) 5)
      eps 0.001
      h0 5000
      x 1.0054]
  (within eps (improve (improve (differentiate h0 fx x)))))
;; => causes an ArithmeticException, Divide by zero in 'order'
;;  the results of the first improve are already so good (the right result) that there is not much to optimize
;;  actually the first element in the lazy-seq is already the result
;;  where the error isn't big enough for a 2nd improvements, maybe different start arguments could help


;; takeaway: it was possible to divide the newton-raphson square root algorithm in smaller peaces.
;;           These peaces/modules can not only changed independently, they're also synchronised due to the laziness.
;;           the relative/within part pulls new values out of the lazy sequence.
;;           The calculation of these new values is triggered on demand.



