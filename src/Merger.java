import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;


public class Merger {
	static int maxBufferSize = 100 * 1024;
	
	public static void kMerge(List<String> files) throws IOException {
		
		List<Chunk> chunks = files.stream().map((file) -> new Chunk(file)).collect(Collectors.toList());
		
				
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/tmp/output.txt"),"UTF-8"), maxBufferSize);
		
		mergeAndWriteChunks(bw, chunks);
		
		bw.close();
		for(Chunk br: chunks) {
			br.close();
		}
	}
	
	private static void mergeAndWriteChunks(BufferedWriter bw, List<Chunk> chunks) throws IOException {
		while(!areBuffersFinished(chunks)) {
			bw.write(getMinLineFromChunks(chunks));
			bw.newLine();
			chunks = chunks.stream().filter(c -> !c.isFinished()).collect(Collectors.toList());
		}
	}
	
	private static boolean areBuffersFinished(List<Chunk> chunks) {
		return chunks.stream().allMatch(chunk -> chunk.isFinished());
	}
	
	private static String getMinLineFromChunks(List<Chunk> chunks) throws IOException {
		return chunks.stream().filter(c -> c.getMinLine() != null).min((c1, c2) -> c1.getMinLine().compareTo(c2.getMinLine())).get().pop();
	}
}
