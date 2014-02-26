(ns core-data-predicates.declarations)

(defn int-file-name-from-class [class] (str class "+_FetchRequests.h"))

(defn imp-file-name-from-class [class] (str class "+_FetchRequests.m"))

(defn inteface-dec [class-name] (str "@interface " class-name " (_FetchRequests)\n\n"))

(defn imp-dec [class-name] (str "@implementation " class-name " (_FetchRequests)\n"))
