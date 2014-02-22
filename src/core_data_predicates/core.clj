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

  (def int-imports (apply str ["#import <CoreData/CoreData.h>\n" "#import <Foundation/Foundation.h>\n\n"]))

  (defn imp-imports [class-name] (apply str ["#import" "\"" class-name "+Operations.h\"\n\n"]))

  (def content (xml-from-file "Model.xcdatamodeld/Model.xcdatamodel/contents"))

  (defn is-equal-from-keypath-int [key-path] (apply str ["+ (NSFetchRequest *)" key-path "IsEqualTo:(id)object;\n"]))

  (defn is-equal-from-keypath-imp-dec [key-path] (apply str ["\n+ (NSFetchRequest *)" key-path "IsEqualTo:(id)object {\n"]))

  (defn is-equal-from-keypath-imp [entity key-path] (imp-with-entity-keypath entity key-path))

  (defn int-file-name-from-class [class] (apply str [class "+Operations.h"]))

  (defn imp-file-name-from-class [class] (apply str [class "+Operations.m"]))

  (defn inteface-dec [class-name] (apply str ["@inteface " class-name "(Operations)\n"]))

  (defn imp-dec [class-name] (apply str ["@implementation " class-name "(Operations)\n"]))

   (doseq [lines content]
     (doseq [other (:content lines)]
       (let [class-name (print-str (:name (:attrs other)))]
       (if-not (.equals class-name "nil")(spit (int-file-name-from-class class-name) int-imports))
       (if-not (.equals class-name "nil")(spit (int-file-name-from-class class-name) (inteface-dec class-name) :append true))
       (if-not (.equals class-name "nil")(spit (imp-file-name-from-class class-name) (imp-imports class-name)))
       (if-not (.equals class-name "nil")(spit (imp-file-name-from-class class-name) (imp-dec class-name) :append true))
       (doseq [final (:content other)]
         (let [key-paths (print-str (:name (:attrs final)))]
         (if-not (.equals class-name "nil")(spit (int-file-name-from-class class-name) (is-equal-from-keypath-int key-paths) :append true))
         (if-not (.equals class-name "nil")(spit (imp-file-name-from-class class-name) (is-equal-from-keypath-imp-dec key-paths) :append true))
         (if-not (.equals class-name "nil")(spit (imp-file-name-from-class class-name) (is-equal-from-keypath-imp class-name key-paths) :append true))))
       )
     )
  )
)
