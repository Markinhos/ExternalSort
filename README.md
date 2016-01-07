# ExternalSort

This is an external sort utility package with Java 8.

It sorts an input file and writes a line sorted output file in path of the second parameter passed. The encoding can be specified,
if not UTF-8 will be used.

In order to make the sorting first it creates several chunks of sorted lines that are able to being sorted in memory. Once written 
all the chunks it opens them and make a k way merge, taking the minimum line of the chunks being compared at the moment.

It supports extremly large lines without a performance penalty, as it reads a buffer reading the lines in it. If the line does not fit into the buffer it is pending 
to be compared, so a Line class is created holding the position in the file where it starts the extremly large line.

The organization of the code is composed in:
* Splitter: Static class in order to split into chunks.
* Sorter: Static class with the external sort method and a quicksort method implementation.
* Merger: Static class in order to merge a list of chunks.
* Chunk: Regular class that holds the information and resources of the chunk (path, reader, lines, etc)
* Line: Regular class that implements Comparable interface. When compared a line that did not fit into the buffer the line compares
 each chunk with the compared line until any of them are different.
 
It is single thread as the external sort is IO bound and parallelizing it wouldn't be a great improvement.

Pending

- Include real unit tests for the splitter and merge.
- Use a proper logger like log4j instead of standard output.
- Check there are not already chunks files with the same name.
