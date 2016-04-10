package uk.co.whatsa.research.presentation;

/**
 * A java bean encapsulating the data of a JSON object used to tail a file.
 */
public class TailedFile {
	/** The last file position read (and the next char to try and read). */
	private long offset;
	
	/** The file data to append to the web page. */
	private String data;
	
	/** The id of the DOM element to be appended. */
	private String selector;
	
	/** The name of the file being tailed. */
	private String fileName;
	
	/** Set if the file could not be tailed. */
	private String errorMessage;

	public long getOffset() {
		return offset;
	}

	public void setOffset(final long offset) {
		this.offset = offset;
	}

	public String getData() {
		return data;
	}

	public void setData(final String data) {
		this.data = data;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
