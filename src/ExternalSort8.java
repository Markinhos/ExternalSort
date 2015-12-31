import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ExternalSort8 {

	public static void main(String[] args) throws IOException {
		List<String> listOfChunks = Splitter.splitFiles();
		for(String name: listOfChunks) {
			sortFile(name);
		}
		Merger.kMerge(listOfChunks);
	}
	
	public static void sortFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/tmp/" + path +".txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/sorted_" + path +".txt"));
		List<String> lines = new ArrayList<String>();
		String line;
		
		while((line = br.readLine()) != null) {
			if (!line.trim().equals(""))
				lines.add(line);
		}
		
		br.close();
		
		Collections.sort(lines);
		
		for(String _line: lines) {
			bw.write(_line);
			bw.newLine();
		}
		bw.close();
	}					

}
