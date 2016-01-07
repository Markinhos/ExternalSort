package tests.sorter;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import sorter.Sorter;

public class SorterTests {

	@Test
	public void testSort() {
		List<String> list = Arrays.asList("foo", "bar", "test");
		Sorter.sort(list);
		assertArrayEquals(list.toArray(), new String[] { "bar", "foo", "test" });
		
	}
	
	@Test
	public void testSortEquals() {
		List<String> list = Arrays.asList("foo", "foo", "foo");
		Sorter.sort(list);
		assertArrayEquals(list.toArray(), new String[] { "foo", "foo", "foo" });	
	}
	
	@Test
	public void testSortRandomNumbers() {
		List<Integer> list = Arrays.asList(genData(10000));
		Sorter.sort(list);
		for(int i = 1; i< list.size(); i++) {
			assertTrue(list.get(i - 1) <= list.get(i));
		}
	}
	
	@Test
	public void testExternalSort() throws IOException {
		String tempFolder = System.getProperty("java.io.tmpdir");
		
		List<String> lines = Arrays.asList("first line", "second line", "a, this goes first");
		String testFile = tempFolder + "test.txt";
		generateFile(testFile, lines);
		
		String outputFile = tempFolder + "testOutput.txt";
		Sorter.externalSort(testFile, outputFile);
		List<String> expectedOutput = Arrays.asList("a, this goes first", "first line", "second line");
		
		int count = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(outputFile));
		String line;
		while((line = br.readLine()) != null) {
			assertEquals(line, expectedOutput.get(count));
			count++;
		}
		br.close();
		
		new File(testFile).delete();
		new File(outputFile).delete();
	}
	
	@Test
	public void testExternalSortUnicode() throws IOException {
		String tempFolder = System.getProperty("java.io.tmpdir");
		
		List<String> lines = Arrays.asList("fîrst line", "a, this gøes first", "second line ñ");
		String testFile = tempFolder + "testUnicode.txt";
		generateFile(testFile, lines);
		
		String outputFile = tempFolder + "testOutputUnicode.txt";
		Sorter.externalSort(testFile, outputFile, "UTF-8");
		List<String> expectedOutput = Arrays.asList("a, this gøes first", "fîrst line", "second line ñ");
		
		int count = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(outputFile));
		String line;
		while((line = br.readLine()) != null) {
			assertEquals(line, expectedOutput.get(count));
			count++;
		}
		br.close();
		
		new File(testFile).delete();
		new File(outputFile).delete();
	}
	
	@Test
	public void testExternalSortUnicodeFail() throws IOException {
		String tempFolder = System.getProperty("java.io.tmpdir");
		
		List<String> lines = Arrays.asList("fîrst line", "a, this gøes first", "second line ñ");
		String testFile = tempFolder + "testUnicodeFail.txt";
		generateFile(testFile, lines);
		
		String outputFile = tempFolder + "testOutputFails.txt";
		Sorter.externalSort(testFile, outputFile, "UTF-16");
		List<String> expectedOutput = Arrays.asList("a, this gøes first", "fîrst line", "second line ñ");
		
		int count = 0;
		
		BufferedReader br = new BufferedReader(new FileReader(outputFile));
		String line;
		while((line = br.readLine()) != null) {
			assertNotEquals(line, expectedOutput.get(count));
			count++;
		}
		br.close();
		
		new File(testFile).delete();
		new File(outputFile).delete();
	}
	
	private Integer[] genData (int len) {
        Random r = new Random();
        Integer[] newData = new Integer[len];
        for (int i = 0; i < newData.length; i++) {
            newData[i] = r.nextInt(100);
        }
        return newData;
    } 
	
	
	private void generateFile(String path, List<String> lines) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(path));
			for (String line: lines) {
				bw.write(line);
				bw.newLine();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bw.close();
		}
	}


}
