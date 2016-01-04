import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Splitter {
//	private static int bufferSize = 100000 * 1024;
	private static int bufferSize = (int) Runtime.getRuntime().freeMemory();
	
	/**
	 * Split the file given in the parameter in a series of sorted chunks.
	 * The size of the chunks are calculated depending on the available memory, getting all the memory free.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> splitFiles(String path) throws IOException {
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			List<String> listOfChunks = writeChunks(br);			
			br.close();
			return listOfChunks;
		}
		
	}
	
	
	private static List<String> writeChunks(BufferedReader br) throws IOException {
		
		List<String> listOfChunks = new ArrayList<String>();
		int numChunks = 1;
		String line;
		int bufferPosition = 0;
				
		
		List<String> lines = new ArrayList<String>();
		while((line = br.readLine()) != null) {
			byte[] lineBytes = line.getBytes();
			if (lineBytes.length + bufferPosition >= bufferSize) {
				listOfChunks.add(sortAndWriteChunk("chunk" + numChunks, lines));
				numChunks += 1;
				lines.clear();
				bufferPosition = 0;
			}
			lines.add(line);
			bufferPosition += lineBytes.length + 1;
		}
				
		listOfChunks.add(sortAndWriteChunk("chunk" + numChunks, lines));
		
		return listOfChunks;
	}
	
	private static void sortChunk(List<String> lines) {
		Sorter.sort(lines);
	}
	
	private static String sortAndWriteChunk(String chunkName, List<String> lines) throws IOException {
		String chunkPath = System.getProperty("java.io.tmpdir") + chunkName + ".txt";
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chunkPath), "UTF-8"), bufferSize);
		

		sortChunk(lines);
		for(String line: lines) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();
		return chunkPath;
	}
}
