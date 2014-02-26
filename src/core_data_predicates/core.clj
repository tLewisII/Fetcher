(ns core-data-predicates.core
  (:gen-class)
  (require [clojure.xml :as xml])
  (require [clojure.zip :as zip])
  (require [clojure.java.io :as io])
  (require [clojure.string :as string])
  (require [core-data-predicates.isEqualInt :as is-equal])
  (require [core-data-predicates.imports :as imports]))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))

  ;; Must pass a model name or nothing will happen
  (if (or (empty? args) (> (count args) 1))
    (do
      (println "No Model name given, you must provide the name of your CoreData model in the form `predicate modelName`")
      (System/exit 0)
      )
    )

  (if-not (.exists (io/as-file (str (first args) ".xcdatamodeld/" (first args) ".xcdatamodel/contents")))
    (do
      (println "No file exists at the given location, check the spelling of your model name")
      (System/exit 0)
      )
    )

  (defn xml-from-file [file] (zip/xml-zip (xml/parse file)))

  (defn imp-with-entity-keypath [entity key-path] (str "\tNSFetchRequest *fetchRequest = [NSFetchRequest fetchRequestWithEntityName:@\""  entity "\"];\n"
                                                              "\t[fetchRequest setPredicate:" "[NSPredicate predicateWithFormat:@\"" key-path " = " "%@\"" ", " "object]];\n"
                                                              "\treturn fetchRequest;\n}\n"))



  (try
    (def content (let [model-name (first args)] (xml-from-file (str model-name ".xcdatamodeld/" model-name ".xcdatamodel/contents"))))
    (catch Exception e (println (.getMessage e)) (System/exit 0))
  )

  (defn is-equal-from-keypath-imp-dec [key-path] (str "\n+ (NSFetchRequest *)" key-path "IsEqualTo:(id)object {\n"))

  (defn is-equal-from-keypath-imp [entity key-path] (imp-with-entity-keypath entity key-path))

  (defn int-file-name-from-class [class] (str class "+_FetchRequests.h"))

  (defn imp-file-name-from-class [class] (str class "+_FetchRequests.m"))

  (defn inteface-dec [class-name] (str "@interface " class-name " (_FetchRequests)\n\n"))

  (defn imp-dec [class-name] (str "@implementation " class-name " (_FetchRequests)\n"))

   (doseq [lines content]
     (doseq [other (:content lines)]
       (when-let [class-name (:name (:attrs other))]
         (spit (int-file-name-from-class class-name) (imports/int-class-import class-name))
         (spit (int-file-name-from-class class-name) imports/int-imports :append true)
         (spit (int-file-name-from-class class-name) (inteface-dec class-name) :append true)
         (spit (imp-file-name-from-class class-name) (imports/imp-imports class-name))
         (spit (imp-file-name-from-class class-name) (imp-dec class-name) :append true)
         (doseq [final (:content other)]
           (let [key-paths (:name (:attrs final))]
           (spit (int-file-name-from-class class-name) (is-equal/is-equal-from-keypath-int key-paths) :append true)
           (spit (imp-file-name-from-class class-name) (is-equal-from-keypath-imp-dec key-paths) :append true)
           (spit (imp-file-name-from-class class-name) (is-equal-from-keypath-imp class-name key-paths) :append true)))
       )
     )
  )

  (doseq [lines content]
     (doseq [other (:content lines)]
        (when-let [class-name (:name (:attrs other))]
          (spit (int-file-name-from-class class-name) "\n@end\n" :append true)
          (spit (imp-file-name-from-class class-name) "\n@end\n" :append true)
          )
       )
    )
)
