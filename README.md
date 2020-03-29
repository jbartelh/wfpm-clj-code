# 'Why Functional Programming Matters' in clojure

['Why Functional Programming Matters' by John Hughes](https://www.google.com)

After a quick time reading the paper, i opened up a clojure REPL and tried out the things described in the paper.
The dynamic of the REPL makes it really easy to play around and try out a lot of things
--- What happens if i change this, could this also work? --- 
This helped me a lot to understand the described concepts and ideas.
It's also really nice how close the clojure syntax is to the definitions in the paper.

# Content

The pseudo code/math defintions in the paper, just translated into clojure.
Combined with a REPL this is kind like an interactive add-on to the paper.

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
