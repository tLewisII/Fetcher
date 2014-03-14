# Fetcher

Generates fetch requests for the entities and properties in a CoreData Model
file. Outputs methods on a category of your entities that look like this:
```
+ (NSArray *)nameIsEqualTo:(id)object inContext:(NSManagedObjectContext *)context sortDescriptors:(NSArray *)sort error:(void(^)(NSError *error))error {
	NSFetchRequest *fetchRequest = [NSFetchRequest fetchRequestWithEntityName:@"TJLSuperEntity"];
	[fetchRequest setPredicate:[NSPredicate predicateWithFormat:@"name = %@", object]];
	[fetchRequest setSortDescriptors:sort];
	NSError *err = nil;
	NSArray *results = [context executeFetchRequest:fetchRequest error:&err];
	if(err && error) {
		error(err);
		return nil;
	}
	return results;
}
```
Stringly typed code is bad, and this does not get rid of it, but it at
least has a machine generate it, which makes it more reliable than typing it
out by hand.

## Installation

The easiest way to currently install Fetcher is to download it and build it from the source. It is written in [Clojure](http://clojure.org), but can be compiled into a .jar file using [leiningen](http://leiningen.org).
Using [Homebrew](http://brew.sh/) simply `brew install leiningen` and once that is finished do run `lein uberjar` which will compile the source into a .jar file that you can use.

## Usage

    $ java -jar Fetcher-0.1.0-standalone.jar [ModelFileName]
Simply pass the name of the Model file you wish to generate requests for, without any extensions and Fetcher will do the rest.

## Options

Currently, there are no options that you can pass, but there might be some in the future. If you have any suggestions, create an issue.

## License

Copyright © 2014 Terry Lewis II

Distributed under the Eclipse Public License, the same as Clojure.
