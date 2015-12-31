import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Chunk{
	private int maxBufferSize = 8 * 1024;
	private boolean isFinished;
	private boolean areMoreBuffers;
	private BufferedReader br;
	private List<String> linesBuffer;
	
	public Chunk(String path) {
		this(path, 8*1024);
	}
	
	public Chunk(String path, int maxBufferSize){
		this.isFinished = false;
		this.areMoreBuffers = true;
		this.linesBuffer = new ArrayList<String>();
		try {
			this.br = new BufferedReader(new FileReader("/tmp/sorted_" + path + ".txt"), maxBufferSize);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.fillBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fillBuffer() throws IOException {
		String line;
		int position = 0;
				
		while((line = br.readLine()) != null && line.getBytes().length + position < maxBufferSize) {
			this.linesBuffer.add(line);
			position += line.getBytes().length;
		}
		
		if (line == null) {
			areMoreBuffers = false;
		} else {
			this.linesBuffer.add(line);
		}
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public String getMinLine() {

		if (isFinished) {
			return null;
		}	
		return linesBuffer.get(0);
		
	}
	
	public String pop() throws IOException {
		String line = linesBuffer.remove(0);		
		
		if (linesBuffer.isEmpty() && areMoreBuffers) {
			try {
				this.fillBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (linesBuffer.isEmpty()){
			this.isFinished = true;
		}
			
		return line;
	}
	
	public void close() throws IOException {
		this.br.close();
	}
}
