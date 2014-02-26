(ns core-data-predicates.isEqualImp)

(defn is-equal-from-keypath-imp-dec [key-path] (str "\n+ (NSFetchRequest *)" key-path "IsEqualTo:(id)object {\n"))

(defn imp-with-entity-keypath [entity key-path] (str "\tNSFetchRequest *fetchRequest = [NSFetchRequest fetchRequestWithEntityName:@\""  entity "\"];\n"
                                                              "\t[fetchRequest setPredicate:" "[NSPredicate predicateWithFormat:@\"" key-path " = " "%@\"" ", " "object]];\n"
                                                              "\treturn fetchRequest;\n}\n"))

(defn is-equal-from-keypath-imp [entity key-path] (imp-with-entity-keypath entity key-path))
