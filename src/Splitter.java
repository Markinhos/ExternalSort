import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Splitter {
	
	private static String defaultEncoding = "UTF-8";
	
	/**
	 * Split the file given in the parameter in a series of sorted chunks.
	 * The size of the chunks are calculated depending on the available memory, getting all the memory free.
	 * @param path Path of the file to sort
	 * @param encoding Encoding to read and write
	 * @return
	 * @throws IOException
	 */
	public static List<String> splitFiles(String path, String encoding) {
		System.out.println("Starting splitting into chunks...");
		List<String> listOfChunks = null;
		long start = System.nanoTime();
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			listOfChunks = writeChunks(br, encoding);
		} catch (FileNotFoundException e) {
			System.err.println("File " + path + " not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Error");
			e.printStackTrace();
		}

		System.out.println("Done splitting.");
		System.out.println("File splitted into " + listOfChunks.size() + " chunks in " + (double)(System.nanoTime() - start) / 1000000000.0 + " seg");

		return listOfChunks;
	}
	
	/**
	 * Split the file given in the parameter in a series of sorted chunks.
	 * The size of the chunks are calculated depending on the available memory, getting all the memory free.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<String> splitFiles(String path){			
		return splitFiles(path, defaultEncoding);		
	}
	
	
	private static List<String> writeChunks(BufferedReader br, String encoding) {

		String tempFolder = System.getProperty("java.io.tmpdir");
		int bufferSize = (int) (Runtime.getRuntime().freeMemory() / 2 < Integer.MAX_VALUE ? Runtime.getRuntime().freeMemory() / 2 : Integer.MAX_VALUE);
		List<String> listOfChunks = new ArrayList<String>();
		int numChunks = 1;
		
		int bytesCount = 0;
		int read, readBufferSize = 8 * 1024;
		StringBuffer sb = new StringBuffer();
		
		char[] cbuf = new char[readBufferSize];
		try {
			while((read = br.read(cbuf)) > 0) {			
				if (bytesCount + read > bufferSize) {
					String chunkPath = tempFolder + "chunk" + numChunks + ".txt";
					if(writeChunk(sb, chunkPath, encoding)){
						numChunks += 1;
						listOfChunks.add(chunkPath);
					}
					bytesCount = 0;
				}
				sb.append(cbuf);
				cbuf = new char[readBufferSize];
				bytesCount += read;
			}
		} catch (IOException e) {
			System.err.println("IO Error");
			e.printStackTrace();
		}
		
		String chunkPath = tempFolder + "chunk" + numChunks + ".txt";
		if(writeChunk(sb, chunkPath, encoding)) {
			listOfChunks.add(chunkPath);
		}
		
		return listOfChunks;
	}
	
	private static boolean writeChunk(StringBuffer sb, String chunkPath, String encoding) {
		int indexOfLastNewLine = sb.lastIndexOf("\n");
		
		if (indexOfLastNewLine >= 0) {			
			sortAndWriteChunk(sb, chunkPath, indexOfLastNewLine, encoding);
			System.out.println("Chunk sorted and written to: " + chunkPath);
			return true;				
		} else {			
			writeFullChunk(sb, chunkPath, encoding);
			System.out.println("Chunk added to file: " + chunkPath);
			return false;
		}
	}
	
	private static void sortAndWriteChunk(StringBuffer sb, String chunkPath, int indexOfLastNewLine, String encoding)  {
		
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chunkPath, true), encoding))) {
			String s = sb.substring(0, indexOfLastNewLine + 1);
			sb.delete(0, indexOfLastNewLine + 1);
			List<String> lines= sortChunk(s);
			for(String line : lines) {
				try {
					bw.write(line);
					bw.newLine();
				} catch (IOException e) {
					System.err.println("Error writing chunk " + chunkPath);
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Encoding " + encoding + " not spported");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("File not found " + chunkPath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception");
			e.printStackTrace();
		}
	}
	
	private static void writeFullChunk(StringBuffer sb, String chunkPath, String encoding) {
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chunkPath, true), encoding))) {
			try {
				bw.write(sb.toString());
				sb.delete(0, sb.length());
				bw.close();
			} catch (IOException e) {
				System.err.println("Error writing chunk " + chunkPath);
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Encoding " + encoding + " not spported");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("File not found " + chunkPath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception");
			e.printStackTrace();
		}
	}
	
	private static List<String> sortChunk(String chunk) {
		List<String> lines = Arrays.asList(chunk.split("\n"));
		Sorter.sort(lines);
		return lines;
	}
}
