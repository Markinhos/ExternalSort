import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;


public class Merger {
	
	/**
	 * Makes a k way merge from the list of files given in the parameters and write it to the output path.
	 * @param files List of files to be merged.
	 * @param outputPath The path where the output file is written.
	 * @throws IOException
	 */
	public static void kMerge(List<String> files, String outputPath) throws IOException {
		
		long maxBufferSize = Runtime.getRuntime().freeMemory() / (files.size() + 1);
		
		List<Chunk> chunks = files.stream().map((file) -> new Chunk(file)).collect(Collectors.toList());
		
				
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "UTF-8"), (int) maxBufferSize);
		
		mergeAndWriteChunks(bw, chunks);
		
		bw.close();
		for(Chunk br: chunks) {
			br.close();
		}
	}
	
	private static void mergeAndWriteChunks(BufferedWriter bw, List<Chunk> chunks) throws IOException {
		while(!areAllBuffersFinished(chunks)) {
			bw.write(getMinLineFromChunks(chunks));
			bw.newLine();
			chunks = chunks.stream().filter(c -> !c.isFinished()).collect(Collectors.toList());
		}
	}
	
	private static boolean areAllBuffersFinished(List<Chunk> chunks) {
		return chunks.stream().allMatch(chunk -> chunk.isFinished());
	}
	
	private static String getMinLineFromChunks(List<Chunk> chunks) throws IOException {
		return chunks.stream().filter(c -> c.getMinLine() != null).min((c1, c2) -> c1.getMinLine().compareTo(c2.getMinLine())).get().pop();
	}
}
