(ns fetcher.core
  (:gen-class)
  (require [clojure.xml :as xml])
  (require [clojure.zip :as zip])
  (require [clojure.java.io :as io])
  (require [clojure.string :as string])
  (require [fetcher.interface :as interface])
  (require [fetcher.imports :as imports])
  (require [fetcher.implementation :as implementation])
  (require [fetcher.declarations :as decl]))

(defn xml-from-file [file] (-> file xml/parse zip/xml-zip))

;; Build a map of operators and method names to work through
(def operators '[{:operator "=" :operation "IsEqualTo"} {:operator ">" :operation "IsGreaterThan"} {:operator "<" :operation "IsLessThan"}
                 {:operator ">=" :operation "GreatThanOrEqualTo"} {:operator "<=" :operation "LesserThanOrEqualTo"}
                 {:operator "!=" :operation "IsNotEqualTo"}])

(def string-operators '[{:operator "BETWEEN" :operation "Between"} {:operator "BEGINSWITH" :operation "BeginsWith"}
                        {:operator "CONTAINS" :operation "Contains"} {:operator "ENDSWITH" :operation "EndsWith"}
                        {:operator "LIKE" :operation "IsLike"} {:operator "MATCHES" :operation "Matches"}])

(defn -main
  [& args]
  ;; Must pass a model name or nothing will happen
  (when (or (empty? args) (> (count args) 1))
    (println "No Model name given, you must provide the name of your CoreData model in the form `predicate modelName`")
    (System/exit 0))

  (when-not (-> (str (first args) ".xcdatamodeld/" (first args) ".xcdatamodel/contents") io/as-file .exists)
    (println "No file exists at the given location, check the spelling of your model name")
    (System/exit 0))

  (try
    (def content (let [model-name (first args)] (xml-from-file (str model-name ".xcdatamodeld/" model-name ".xcdatamodel/contents"))))
    (catch Exception e (println (.getMessage e)) (System/exit 0)))

  (doseq [lines content]
    (doseq [other (:content lines)]
      (when-let [class-name (-> other :attrs :name)]
        (spit (decl/int-file-name-from-class class-name) (imports/int-class-import class-name))
        (spit (decl/int-file-name-from-class class-name) imports/int-imports :append true)
        (spit (decl/int-file-name-from-class class-name) (decl/inteface-dec class-name) :append true)
        (spit (decl/imp-file-name-from-class class-name) (imports/imp-imports class-name))
        (spit (decl/imp-file-name-from-class class-name) (decl/imp-dec class-name) :append true)
        (doseq [final (:content other)]
          (let [key-paths (-> final :attrs :name) attributeType (-> final :attrs :attributeType) relationship (-> final :attrs :toMany)]
            (when (nil? relationship)
              (doseq [operator operators]
                (spit (decl/int-file-name-from-class class-name) (interface/is-equal-from-keypath-int key-paths (:operation operator)) :append true)
                (spit (decl/imp-file-name-from-class class-name) (implementation/is-equal-from-keypath-imp-dec key-paths (:operation operator)) :append true)
                (spit (decl/imp-file-name-from-class class-name) (implementation/is-equal-from-keypath-imp class-name key-paths (:operator operator)) :append true))
              (if (.equals "String" attributeType)
                (doseq [string-op string-operators]
                  (spit (decl/int-file-name-from-class class-name) (interface/is-equal-from-keypath-int key-paths (:operation string-op)) :append true)
                  (spit (decl/imp-file-name-from-class class-name) (implementation/is-equal-from-keypath-imp-dec key-paths (:operation string-op)) :append true)
                  (spit (decl/imp-file-name-from-class class-name) (implementation/is-equal-from-keypath-imp class-name key-paths (:operator string-op)) :append true)))))))))

  ;; create the @end directive at the end of the file
  (doseq [lines content]
    (doseq [other (:content lines)]
      (when-let [class-name (-> other :attrs :name)]
        (spit (decl/int-file-name-from-class class-name) "\n@end\n" :append true)
        (spit (decl/imp-file-name-from-class class-name) "\n@end\n" :append true)))))
