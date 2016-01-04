import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Main {

	public static void main(String[] args) throws IOException {
//		Integer[] s = new Integer[] { 2, 2, 2 };
//		int num = 10;
//		List<Integer> ls = Arrays.asList(genData(num));
//		long start = System.nanoTime();
//		int[] data = genDataInt(num);
//		Sorter.sort(ls);
//		long duration = System.nanoTime() - start;
//		for(Integer i = 1; i < ls.size(); i++) {
//			if (ls.get(i - 1) > ls.get(i)) {
//				System.out.println("Wrong ordering! " + ls.get(i - 1) + " " + ls.get(i));
//			}
//		}
////		for(Integer i = 1; i < data.length; i++) {
////			if (data[i - 1] > data[i]) {
////				System.out.println("Wrong ordering! " + ls.get(i - 1) + " " + ls.get(i));
////			}
////		}
//		System.out.println("Duration " + duration);
//
//		List<Integer> ls2 = Arrays.asList(genData(num));
//		long start2 = System.nanoTime();
//		Collections.sort(ls2);
//		long duration2 = System.nanoTime() - start2;
//		System.out.println("Duration2 " + duration2);
		
		long start = System.nanoTime();
		List<String> listOfChunks = Splitter.splitFiles("/tmp/bigfile2.txt");
		long splitTime = System.nanoTime() - start;
		System.out.println("Splitted and sorted in " + (double)splitTime / 1000000000.0);
		Merger.kMerge(listOfChunks, "/tmp/output.txt");
		long duration =  (System.nanoTime() - start);
		System.out.println("Duration " + (double)duration / 1000000000.0);
	}
	
	private static Integer[] genData (int len) {
        Random r = new Random();
        Integer[] newData = new Integer[len];
        for (int i = 0; i < newData.length; i++) {
            newData[i] = r.nextInt(100);
        }
        return newData;
    } 
	
	private static int[] genDataInt (int len) {
        Random r = new Random();
        int[] newData = new int[len];
        for (int i = 0; i < newData.length; i++) {
            newData[i] = r.nextInt(100);
        }
        return newData;
    }
}
