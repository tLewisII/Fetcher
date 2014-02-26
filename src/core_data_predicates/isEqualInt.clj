(ns core-data-predicates.isEqualInt)

(defn is-equal-from-keypath-int [key-path] (str "+ (NSFetchRequest *)" key-path "IsEqualTo:(id)object;\n"))
