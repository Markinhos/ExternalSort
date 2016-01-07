package sorter;
import java.util.List;


public class Sorter{
	
	private static String defaultEncoding = "UTF-8";
	
	/**
	 * Sorts the lines of a file in increasing order.
	 * @param inputPath Input file to sort.
	 * @param outputPath Output path.
	 * @param encoding Encoding to read and write.
	 */
	public static void externalSort(String inputPath, String outputPath, String encoding) {
		long start = System.nanoTime();
		List<String> listOfChunks = Splitter.splitFiles(inputPath, encoding);
		Merger.kMerge(listOfChunks, outputPath, encoding);
		System.out.println("Duration in " + (double)(System.nanoTime() - start) / 1000000000.0 + " seg");
	}
	
	/**
	 * Sorts the lines of a file in increasing order. Uses UTF-8 encoding.
	 * @param inputPath Input file to sort.
	 * @param outputPath Output path.
	 */
	public static void externalSort(String inputPath, String outputPath) {
		externalSort(inputPath, outputPath, defaultEncoding);
	}
	
	/**
	 * Sort a list of comparable items in place.
	 * This method uses an implementation of quicksort algorithm.
	 * @param list List of items. All items in the list have to implements Comparable and comparable among them. 
	 */
	public static <E extends Comparable<E>> void sort(List<E> list) {
		quickSort(list, 0, list.size() - 1);
	}
	
	private static <E extends Comparable<E>> void quickSort(List<E> list, int beginning, int end) {
		
		if(end > beginning) {
			int partitionPoint = partition(list, beginning, end);
			if (partitionPoint > beginning) {
				quickSort(list, beginning, partitionPoint - 1);
			}
			if (partitionPoint < end) {
				quickSort(list, partitionPoint, end);
			}
		}
	}
	
	private static <E extends Comparable<E>> int partition(List<E> list, int beginning, int end) {
		E pivot = list.get(beginning);
		int i = beginning, j = end;
		
		while(i <= j) {
			while(list.get(i).compareTo(pivot) < 0) {
				i += 1;
			}
			while (list.get(j).compareTo(pivot) > 0) {
				j -= 1;
			}
			if (i <= j) {
				swap(list, i, j);
				i += 1;
				j -= 1;
			}
		}
		return i;
	}
	
	
	private static <E extends Comparable<E>> void swap(List<E> list, int x, int y) {
		E aux = list.get(y);
		list.set(y, list.get(x));
		list.set(x, aux);
	}
	
	
}
