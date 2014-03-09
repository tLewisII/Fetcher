(ns core-data-predicates.implementation)

(defn is-equal-from-keypath-imp-dec [key-path operation] (str "\n+ (NSArray *)" key-path operation ":(id)object inContext:(NSManagedObjectContext *)context"
                                                              " sortDescriptors:(NSArray *)sort error:(void(^)(NSError *error))error {\n"))

(defn imp-with-entity-keypath [entity key-path operator] (str "\tNSFetchRequest *fetchRequest = [NSFetchRequest fetchRequestWithEntityName:@\""  entity "\"];\n"
                                                              "\t[fetchRequest setPredicate:" "[NSPredicate predicateWithFormat:@\"" key-path " " operator  " %@\"" ", " "object]];\n"
                                                              "\t[fetchRequest setSortDescriptors:sort];\n"
                                                              "\tNSError *err = nil;\n"
                                                              "\tNSArray *results = [context executeFetchRequest:fetchRequest error:&err];\n"
                                                              "\tif(err && error) {\n"
                                                              "\t\terror(err);\n"
                                                              "\t\treturn nil;\n"
                                                              "\t}\n"
                                                              "\treturn results;\n}\n"))

(defn is-equal-from-keypath-imp [entity key-path operator] (imp-with-entity-keypath entity key-path operator))
