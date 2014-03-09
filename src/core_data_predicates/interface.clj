(ns core-data-predicates.interface)

(defn is-equal-from-keypath-int [key-path operation] (str "+ (NSArray *)" key-path operation
                                                          ":(id)object inContext:(NSManagedObjectContext *)context"
                                                          " sortDescriptors:(NSArray *)sort error:(void(^)(NSError *error))error;\n\n"))
