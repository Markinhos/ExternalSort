import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Chunk{
	private int bufferSize;
	private boolean isFinished;
	private boolean areMoreBuffers;
	private BufferedReader br;
	private Queue<Line> linesBuffer;
	private StringBuffer sb;
	private String path;
	private long positionInFile;
	private boolean pendingToWrite;
	
	public Chunk(String path) {
		this(path, 8*1024);
	}
	
	public Chunk(String path, int maxBufferSize){
		this.isFinished = false;
		this.pendingToWrite = false;
		this.areMoreBuffers = true;
		this.linesBuffer = new ArrayDeque<Line>();
		this.bufferSize = maxBufferSize;
		this.sb = new StringBuffer();
		this.path = path;
		this.positionInFile = 0;
		
		try {
			this.br = new BufferedReader(new FileReader(path), maxBufferSize);
		} catch (FileNotFoundException e) {
			System.err.println("Could not open path " + path);
			e.printStackTrace();
		}
		try {
			this.fillBuffer();
		} catch (IOException e) {
			System.err.println("Error filling buffer");
			e.printStackTrace();
		}
	}
	
	private void fillBuffer() throws IOException {
		
		int read = sb.length();
		
		char[] cbuf = new char[bufferSize];
		read = br.read(cbuf);
		if (read < 0) {
			areMoreBuffers = false;
			this.br.close();
			deleteChunk();
		} else {
			sb.append(cbuf, 0, read);
			this.linesBuffer.addAll(getLinesFromStringBuffer(sb));
			positionInFile += read;
			
			if (linesBuffer.isEmpty()){
				this.isFinished = true;
			} else {
				this.isFinished = false;
			}
		}
		
	}
	
	private List<Line> getLinesFromStringBuffer(StringBuffer sb) {
		int indexOfLastNewLine = sb.lastIndexOf("\n");
		
		if (indexOfLastNewLine >= 0) {
			String s = sb.substring(0, indexOfLastNewLine + 1);
			sb.delete(0, indexOfLastNewLine + 1);
			return Arrays.asList(s.split("\n")).stream().map((line) -> new Line(line)).collect(Collectors.toList());
		} else {
			Line l = new Line(sb.toString(), path, positionInFile, bufferSize);
			sb.delete(0, sb.length());
			return Arrays.asList(l);
		}
	}
	
	/**
	 * Indicate if the chunk has no more lines.
	 * @return true if no more lines available.
	 */
	public boolean isFinished() {
		return isFinished;
	}
	
	/**
	 * Gets the first line from the list of lines.
	 * @return Gives the first line if available. Null otherwise.
	 */
	public Line getMinLine() {

		if (isFinished) {
			return null;
		}	
		return linesBuffer.peek();
		
	}
	
	/**
	 * Returns the first line and removes it from the list.
	 * @return The first line.
	 * @throws IOException
	 */
	public Line pop() {
		Line line = linesBuffer.poll();
		
		if (!line.isLinePendingToWrite() && linesBuffer.isEmpty() && areMoreBuffers) {
			try {
				this.fillBuffer();
			} catch (IOException e) {
				System.err.println("Error filling buffer");
				e.printStackTrace();
			}
		}
		
		if (line.isLinePendingToWrite()) {
			this.pendingToWrite = true;
		}
		
		if(linesBuffer.isEmpty() && !areMoreBuffers) {
			this.isFinished = true;
		}
			
		return line;
	}
	
	public String getNextStringChunk() throws IOException {
		int read;
		char[] cbuf = new char[bufferSize];

		if (!this.pendingToWrite) {
			this.fillBuffer();
			return null;
		}
		
		read = this.br.read(cbuf);
		if (read < 0) {
			this.isFinished = true;
			return null;
		} 
		
		String s = new String(cbuf, 0, read);
		
		int indexOfNewLine = s.indexOf("\n");
		
		String chunk;
		if (indexOfNewLine > 0) {
			chunk = sb.append(s.substring(0, indexOfNewLine)).toString();
			sb.delete(0, sb.length());
			sb.append(s.substring(indexOfNewLine + 1));
			this.pendingToWrite = false;
		} else {
			chunk = sb.append(s).toString();
			sb.delete(0, sb.length());
			this.pendingToWrite = true;
		}
		return chunk;
	}
	
	private boolean deleteChunk() {
		System.out.println("Done with chunk " + this.path + ". Removing from disk...");
		return (new File(this.path)).delete();
	}
}
