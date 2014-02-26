(ns core-data-predicates.core
  (:gen-class)
  (require [clojure.xml :as xml])
  (require [clojure.zip :as zip])
  (require [clojure.java.io :as io])
  (require [clojure.string :as string])
  (require [core-data-predicates.isEqualInt :as is-equal])
  (require [core-data-predicates.imports :as imports])
  (require [core-data-predicates.isEqualImp :as is-equal-imp])
  (require [core-data-predicates.declarations :as decl]))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  ;; Build a map of operators and method names to work through
  ;;(def operators '["=" "<" ">" "<=" ">=" "!=" "BETWEEN" "BEGINSWITH" "CONTAINS" "ENDSWITH" "LIKE" "MATCHES"])
  ;; Must pass a model name or nothing will happen
  (if (or (empty? args) (> (count args) 1))
    (do
      (println "No Model name given, you must provide the name of your CoreData model in the form `predicate modelName`")
      (System/exit 0)
      )
    )

  (if-not (-> (str (first args) ".xcdatamodeld/" (first args) ".xcdatamodel/contents") io/as-file .exists)
    (do
      (println "No file exists at the given location, check the spelling of your model name")
      (System/exit 0)
      )
    )

  (defn xml-from-file [file] (-> file xml/parse zip/xml-zip))

  (try
    (def content (let [model-name (first args)] (xml-from-file (str model-name ".xcdatamodeld/" model-name ".xcdatamodel/contents"))))
    (catch Exception e (println (.getMessage e)) (System/exit 0))
  )

   (doseq [lines content]
     (doseq [other (:content lines)]
       (when-let [class-name (-> other :attrs :name)]
         (spit (decl/int-file-name-from-class class-name) (imports/int-class-import class-name))
         (spit (decl/int-file-name-from-class class-name) imports/int-imports :append true)
         (spit (decl/int-file-name-from-class class-name) (decl/inteface-dec class-name) :append true)
         (spit (decl/imp-file-name-from-class class-name) (imports/imp-imports class-name))
         (spit (decl/imp-file-name-from-class class-name) (decl/imp-dec class-name) :append true)
         (doseq [final (:content other)]
           (let [key-paths (-> final :attrs :name)]
           (spit (decl/int-file-name-from-class class-name) (is-equal/is-equal-from-keypath-int key-paths) :append true)
           (spit (decl/imp-file-name-from-class class-name) (is-equal-imp/is-equal-from-keypath-imp-dec key-paths) :append true)
           (spit (decl/imp-file-name-from-class class-name) (is-equal-imp/is-equal-from-keypath-imp class-name key-paths) :append true)))
       )
     )
  )

  (doseq [lines content]
     (doseq [other (:content lines)]
        (when-let [class-name (-> other :attrs :name)]
          (spit (decl/int-file-name-from-class class-name) "\n@end\n" :append true)
          (spit (decl/imp-file-name-from-class class-name) "\n@end\n" :append true)
          )
       )
    )
)
