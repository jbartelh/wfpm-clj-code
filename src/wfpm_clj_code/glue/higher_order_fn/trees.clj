(ns wfpm-clj-code.glue.higher-order-fn.trees)

;; Why Functional Programming Matters - John Hughes
;; Section about function on trees

;; the tree-structure definition
(defn node [label subtree] {:label label :subtree subtree})

;; used to build the example tree
#_(def tree-build-with-fns (node 1 (cons (node 2 nil)
                                         (cons (node 3
                                                     (cons (node 4 nil) nil))
                                               nil))))

;; same tree build just with normal clojure
(def tree
  {:label   1
   :subtree [
             {:label   2
              :subtree nil},
             {:label   3
              :subtree [
                        {:label   4
                         :subtree nil}
                        ]}
             ]})

;; just to proof that they're the same
#_(= tree tree-build-with-fns)

;; the 'foldtree f g a'-function
;; could be optimized, especially the vector? and the nested-if parts, maybe pattern-matching will help.

(defn fold-tree [f g val tree]
  (if (nil? tree) val
                  (if (vector? tree)
                    (let [[subtree & rest] tree]
                      (g (fold-tree f g val subtree) (fold-tree f g val (when rest (vec rest)))))
                    (f (:label tree) (fold-tree f g val (:subtree tree))))))

;; using fold-tree
(fold-tree + + 0 tree)

(def sum-tree (partial fold-tree + + 0))
(sum-tree tree)

(def labels (partial fold-tree cons concat nil))
(labels tree)

;; the 'maptree f'-function
;; applies f to all labels
;; realized just by gluing existing functions together, e.g. fold-tree, node, ...

(def map-tree (fn [f tree] (fold-tree #(node (f %1) %2) cons nil tree)))

(def double-labels (partial map-tree #(* 2 %)))
(double-labels tree)

;; some stuff i used to find bugs in my earlier fold-tree implementations
;; because i couldn't distinguish between a (foldtree f g val [node1 node2 .. nodeN]) and
;; (foldtree f g a val someNode) it caused an infinite loop
;; afterwards i've added the vector? and (vec rest) parts to avoid an infinite loop

#_(let [[subtree & rest] [{:label 4 :subtree nil}]]
    (str "subtree: " subtree " rest: " rest))

#_(+ (foldTree + + 0 {:label 4 :subtree nil})
     (foldTree + + 0 nil))




;; some map and filter play-a-round

(def someNumbers [2 83 3 83 9 6 782 923 820 2])

(filter odd? (filter #(< 100 %) (map #(+ 5 %) someNumbers)))

(def number->plus5 (partial #(+ 5 %)))
(def lessThen100? (partial #(< 100 %)))

(->> someNumbers
     (map number->plus5)
     (filter lessThen100?)
     (filter odd?))