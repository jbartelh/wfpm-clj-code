# 'Why Functional Programming Matters' in clojure

['Why Functional Programming Matters' by John Hughes](https://www.cs.kent.ac.uk/people/staff/dat/miranda/whyfp90.pdf)

After a quick time reading the paper, i opened up a clojure REPL and tried out the things described in the paper.
The dynamic of the REPL makes it really easy to play around and try out a lot of things
--- What happens if i change this, could this also work? --- 
This helped me a lot to understand the described concepts and ideas.
It's also really nice how close the clojure syntax is to the definitions in the paper.

This repository contains pseudo code/math definitions from the paper as _Clojure_ code.
Combined with a REPL this is kind like an interactive add-on to the paper.

## Content

 _3 Gluing Functions Together_
 - src/wfpm_clj_code/glue/higher_order_fn
   - lists.clj
   - trees.clj
   
 _4 Gluing Programs Together_
 - src/wfpm_clj_code/glue/lazy_evaluation
   - lazy_seq

## How to run

It's a leiningen project. Without an IDE just run the following in the project dir:
```
lein repl
```
load a file:
```
(load-file "src/wfpm_clj_code/glue/higher_order_fn/trees.clj")
```
switch to its namespace, e.g.:
```
(in-ns 'wfpm-clj-code.glue.higher-order-fn.trees)
```
and start eval some expressions:
```
(fold-tree + + 0 tree)
=> 10
```
