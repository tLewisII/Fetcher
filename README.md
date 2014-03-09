# core-data-predicates

Generates fetch requests for the entities and properties in a CoreData Model
file. Stringly typed code is bad, and this does not get rid of it, but it at
least has a machine generate it, which makes it more reliable than typing it
out by hand.

## Installation

The easiest way to currently install core-data-predicates is to download it and build it from the source. It is written in [Clojure](http://clojure.org), but can be compiled into a .jar file using [leiningen](http://leiningen.org).
Using [Homebrew](http://brew.sh/) simply `brew install leiningen` and once that is finished do run `lein uberjar` which will compile the source into a .jar file that you can use.

## Usage

FIXME: explanation

    $ java -jar core-data-predicates-0.1.0-standalone.jar [ModelFileName]
Simply pass the name of the Model file you wish to generate requests for, without any extensions and core-data-predicates will do the rest.

## Options

Currently, there are no options available, but there might be some in the future. If you have any suggestions, create an issue.

### Bugs

Not exactly a bug, but currently requests are generated for to-many relationships, where in practice I would imagine these would not ever be used.

## License

Copyright © 2014 Terry Lewis II

Distributed under the Eclipse Public License, the same as Clojure.
