import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Splitter {
	private static int bufferSize = 100 * 1024;
	
	public static List<String> splitFiles() throws IOException {
		List<String> listOfChunks = new ArrayList<String>();
		int numChunks = 1;
		
		BufferedReader br = new BufferedReader(new FileReader("/tmp/smallfile.txt"));
		
		String line;
		int bufferPosition = 0;
		
		List<String> lines = new ArrayList<String>();
		while((line = br.readLine()) != null) {
			byte[] lineBytes = line.getBytes();
			if (lineBytes.length + bufferPosition >= bufferSize) {
				writeChunk("chunk" + numChunks, lines);
				listOfChunks.add("chunk" + numChunks);
				numChunks += 1;
				lines.clear();
				bufferPosition = 0;
			} 
			lines.add(line);
			bufferPosition += lineBytes.length + 1;
		}
		
		writeChunk("chunk" + numChunks, lines);
		listOfChunks.add("chunk" + numChunks);
		br.close();
		return listOfChunks;
	}
	
	public static void writeChunk(String chunkName, List<String> lines) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/" + chunkName + ".txt"), bufferSize);
		
		for(String line: lines) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();
	}
}
