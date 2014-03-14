(ns fetcher.imports)

(defn int-class-import [class-name] (str "#import \"" class-name ".h\"\n"))

(def int-imports (str "#import <CoreData/CoreData.h>\n" "#import <Foundation/Foundation.h>\n\n"))

(defn imp-imports [class-name] (str "#import" "\"" class-name "+_FetchRequests.h\"\n\n"))
