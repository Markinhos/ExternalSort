import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;


public class Merger {
	
	private final static String defaultEncoding = "UTF-8";
	private final static int bufferSize = 64 * 1000;
	
	/**
	 * Makes a k way merge from the list of files given in the parameters and write it to the output path.
	 * @param files List of files to be merged.
	 * @param outputPath The path where the output file is written.
	 * @param encoding The encoding of the text to read and write.
	 * @throws IOException
	 */
	public static void kMerge(List<String> files, String outputPath, String encoding) {
		long start = System.nanoTime();
		System.out.println("Starting to merge...");
		
		List<Chunk> chunks = files.stream().map((file) -> new Chunk(file, bufferSize)).collect(Collectors.toList());
	
				
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), encoding))){		
			mergeAndWriteChunks(bw, chunks);			
			bw.close();
		} catch (UnsupportedEncodingException e) {
			System.err.println("Encoding " + encoding + " not spported");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("File not found " + outputPath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception");
			e.printStackTrace();
		}

		System.out.println("Merged in " + (double)(System.nanoTime() - start) / 1000000000.0 + " seg");
	}
	
	/**
	 * Makes a k way merge from the list of files given in the parameters and write it to the output path.
	 * @param files List of files to be merged.
	 * @param outputPath The path where the output file is written.
	 * @throws IOException
	 */
	public static void kMerge(List<String> files, String outputPath) {
		kMerge(files, outputPath, defaultEncoding);
	}
	
	private static void mergeAndWriteChunks(BufferedWriter bw, List<Chunk> chunks) throws IOException {
		while(!areAllBuffersFinished(chunks)) {
			Chunk chunk = getMinChunkFromChunks(chunks);
			Line l = chunk.pop();
			bw.write(l.getLine());
			if (l.isLinePendingToWrite()) {
				String toWrite;
				while((toWrite = chunk.getNextStringChunk()) != null) {
					bw.write(toWrite);
				}				
			}
			bw.newLine();
			chunks = chunks.stream().filter(c -> !c.isFinished()).collect(Collectors.toList());
		}
	}
	
	private static boolean areAllBuffersFinished(List<Chunk> chunks) {
		return chunks.stream().allMatch(chunk -> chunk.isFinished());
	}
	
	private static Chunk getMinChunkFromChunks(List<Chunk> chunks) throws IOException {
		return chunks.stream().min((c1, c2) -> c1.getMinLine().compareTo(c2.getMinLine())).get();
	}
}
