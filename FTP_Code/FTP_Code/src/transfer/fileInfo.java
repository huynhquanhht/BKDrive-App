package transfer;

import java.io.Serializable;

public class fileInfo implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long filesize;
	private String filename;
	private String dir;
	
	
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public fileInfo(String filename, long filesize, String dir)
	{
		setFilename(filename);
		setFilesize(filesize);
		setDir(dir);
	}
	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	
}

