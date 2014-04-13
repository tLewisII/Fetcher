(ns fetcher.declarations)

(defn int-file-name-from-class [class-name] (str class-name "+_FetcherRequests.h"))

(defn imp-file-name-from-class [class-name] (str class-name "+_FetcherRequests.m"))

(defn inteface-dec [class-name] (str "@interface " class-name " (_FetcherRequests)\n\n"))

(defn imp-dec [class-name] (str "@implementation " class-name " (_FetcherRequests)\n"))
