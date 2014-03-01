(ns core-data-predicates.isEqualImp)

(defn is-equal-from-keypath-imp-dec [key-path operation] (str "\n+ (NSFetchRequest *)" key-path operation ":(id)object {\n"))

(defn imp-with-entity-keypath [entity key-path operator] (str "\tNSFetchRequest *fetchRequest = [NSFetchRequest fetchRequestWithEntityName:@\""  entity "\"];\n"
                                                              "\t[fetchRequest setPredicate:" "[NSPredicate predicateWithFormat:@\"" key-path " " operator  " %@\"" ", " "object]];\n"
                                                              "\treturn fetchRequest;\n}\n"))

(defn is-equal-from-keypath-imp [entity key-path operator] (imp-with-entity-keypath entity key-path operator))
