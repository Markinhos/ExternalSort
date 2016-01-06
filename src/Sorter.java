import java.util.List;


public class Sorter{
	
	public static void sort(String inputPath, String outputPath) {
		long start = System.nanoTime();
		List<String> listOfChunks = Splitter.splitFiles(inputPath);
		Merger.kMerge(listOfChunks, outputPath);
		System.out.println("Duration in " + (double)(System.nanoTime() - start) / 1000000000.0 + " seg");
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
