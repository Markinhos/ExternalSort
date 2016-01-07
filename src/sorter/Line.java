package sorter;
import java.io.IOException;
import java.io.RandomAccessFile;


public class Line implements Comparable<Line> {

	private String line;
	private long initialPosition;
	private long currentPosition;
	private String path;
	private boolean pendingWrite;
	private int bufferSize;
	private String encoding;

	@Override
	public int compareTo(Line o) {
		int comparisionBetweenLines = this.line.compareTo(o.getLine());
		
		if (comparisionBetweenLines == 0 && this.path != null) {
			String thisNextChunk;
			String otherNextChunk = null;
			while((thisNextChunk = this.getNextChunkToCompare()) != null && (otherNextChunk = o.getNextChunkToCompare()) != null) {
				comparisionBetweenLines = thisNextChunk.compareTo(otherNextChunk);
				if (comparisionBetweenLines != 0) {
					this.resetPosition();
					o.resetPosition();
					return comparisionBetweenLines;
				}					
			}

			this.resetPosition();
			o.resetPosition();
			
			if(thisNextChunk == null) {
				return -1;
			} 
			
			if(otherNextChunk == null) {
				return 1;
			}
			return 0;
			
		} 
		return comparisionBetweenLines;
	}	
	
	public void resetPosition() {
		this.currentPosition = initialPosition;
	}
	
	public String getLine() {
		return this.line;
		
	}

	public Line(String line, String encoding) {
		this.line = line;
		this.pendingWrite = false;
		this.encoding = encoding;
	}
	
	public Line(String line) {
		this(line, "UTF-8");
	}
	
	public Line(String line, String path, long position, int bufferSize) {
		this.line = line;
		this.path = path;
		this.initialPosition = position;
		this.currentPosition = initialPosition;
		this.pendingWrite = true;
		this.bufferSize = bufferSize;
	}
	
	private String getNextChunkToCompare() {
		int read;
		String chunk = null;
		try(RandomAccessFile rf = new RandomAccessFile(this.path, "r")) {
			rf.seek(this.currentPosition);
			byte[] buffer = new byte[bufferSize];
			if ((read = rf.read(buffer)) < 0) {
				return null;
			}
			this.currentPosition += read;
			String s = new String(buffer, this.encoding);
			
			int indexOfLastNewLine = s.indexOf("\n");
			
			if (indexOfLastNewLine > 0) {
				chunk = s.substring(0, indexOfLastNewLine + 1);
			} else {
				chunk = s;
			}
		}
		catch(IOException ex) {
			System.err.println("Could not open chunk" + this.path);
			ex.printStackTrace();
		}
		return chunk;
	}			
	
	public boolean isLinePendingToWrite() {
		return this.pendingWrite;
	}

}
