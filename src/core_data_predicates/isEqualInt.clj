(ns core-data-predicates.isEqualInt)

(defn is-equal-from-keypath-int [key-path operation] (str "+ (NSFetchRequest *)" key-path operation
                                                          ":(id)object inContext:(NSManagedObjectContext *)context;\n"))
