(ns core-data-predicates.core
  (:gen-class)
  (require [clojure.xml :as xml])
  (require [clojure.zip :as zip])
  (require [clojure.java.io])
  (require [clojure.string :as string]))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))

  (defn xml-from-file [file] (zip/xml-zip (xml/parse file)))

  (defn imp-with-entity-keypath [entity key-path] (apply str ["\tNSFetchRequest *fetchRequest = [NSFetchRequest fetchRequestWithEntityName:@\""  entity "\"];\n"
                                                              "\t[fetchRequest setPredicate:" "[NSPredicate predicateWithFormat:@\"" key-path " = " "%@\"" ", " "object]];\n"
                                                              "\treturn fetchRequest;\n}\n"]))

  (defn int-class-import [class-name] (apply str ["#import \"" class-name ".h\"\n"]))

  (def int-imports (apply str ["#import <CoreData/CoreData.h>\n" "#import <Foundation/Foundation.h>\n\n"]))

  (defn imp-imports [class-name] (apply str ["#import" "\"" class-name "+_FetchRequests.h\"\n\n"]))

  (def content (xml-from-file "Model.xcdatamodeld/Model.xcdatamodel/contents"))

  (defn is-equal-from-keypath-int [key-path] (apply str ["+ (NSFetchRequest *)" key-path "IsEqualTo:(id)object;\n"]))

  (defn is-equal-from-keypath-imp-dec [key-path] (apply str ["\n+ (NSFetchRequest *)" key-path "IsEqualTo:(id)object {\n"]))

  (defn is-equal-from-keypath-imp [entity key-path] (imp-with-entity-keypath entity key-path))

  (defn int-file-name-from-class [class] (apply str [class "+_FetchRequests.h"]))

  (defn imp-file-name-from-class [class] (apply str [class "+_FetchRequests.m"]))

  (defn inteface-dec [class-name] (apply str ["@interface " class-name " (_FetchRequests)\n\n"]))

  (defn imp-dec [class-name] (apply str ["@implementation " class-name " (_FetchRequests)\n"]))

   (doseq [lines content]
     (doseq [other (:content lines)]
       (when-let [class-name (:name (:attrs other))]
         (spit (int-file-name-from-class (print-str class-name)) (int-class-import class-name))
         (spit (int-file-name-from-class (print-str class-name)) int-imports :append true)
         (spit (int-file-name-from-class (print-str class-name)) (inteface-dec (print-str class-name)) :append true)
         (spit (imp-file-name-from-class (print-str class-name)) (imp-imports (print-str class-name)))
         (spit (imp-file-name-from-class class-name) (imp-dec (print-str class-name)) :append true)
         (doseq [final (:content other)]
           (let [key-paths (print-str (:name (:attrs final)))]
           (spit (int-file-name-from-class (print-str class-name)) (is-equal-from-keypath-int key-paths) :append true)
           (spit (imp-file-name-from-class (print-str class-name)) (is-equal-from-keypath-imp-dec key-paths) :append true)
           (spit (imp-file-name-from-class (print-str class-name)) (is-equal-from-keypath-imp (print-str class-name) key-paths) :append true)))
       )
     )
  )

  (doseq [lines content]
     (doseq [other (:content lines)]
        (when-let [class-name (:name (:attrs other))]
          (spit (int-file-name-from-class class-name) "\n@end\n" :append true)
          (spit (imp-file-name-from-class class-name) "\n@end\n" :append true)))
       )
)
