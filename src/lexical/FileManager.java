package lexical;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class FileManager implements AutoCloseable{
	private String filename;
    private PushbackInputStream inputStream;
    private boolean endOfFile;

    public FileManager(String filename) throws IOException {
    	this.filename = filename;
    	this.openInputStream();
    	this.endOfFile = false;
    }

    public int read() throws IOException {
    	int ch = this.inputStream.read();
        if (ch == -1) {
            this.endOfFile = true;
        }
        return ch;
    }

    public void unget(int ch) throws IOException {
    	this.inputStream.unread(ch);
        if (ch != -1) {
        	this.endOfFile = false;
        }
    }

	@Override
	public void close() throws IOException {
		this.inputStream.close();
	}
	
	public void reset() throws IOException {
        this.close();
        this.openInputStream();
    }
	
	public boolean endOfFileReached() {
		return this.endOfFile;
	}
	
	private void openInputStream() throws IOException {
        inputStream = new PushbackInputStream(new FileInputStream(this.filename));
        this.endOfFile = false;
    }
}
