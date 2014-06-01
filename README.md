orb\_troll
==========

A Clojure library designed to calculate the odds of success on a random
board

Usage
-----

```sh
git clone https://github.com/brymck/orb_troll
cd orb_troll
lein repl
```

And in the REPL:

```clojure
(load-file "orb_troll.clj")
(simulate ra)
;; => 0.54473
user=> (simulate ra mastering vampire)
;; => 0.92155
```

License
-------

Copyright © 2014 brymck

Distributed under the MIT License
