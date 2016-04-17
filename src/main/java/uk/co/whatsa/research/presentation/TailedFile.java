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

	/**
	 * @return {@link #offset}
	 */
	public final long getOffset() {
		return offset;
	}

	/**
	 * @param offset {@link #offset}
	 */
	public final void setOffset(final long offset) {
		this.offset = offset;
	}

	/**
	 * @return {@link #data}
	 */
	public final String getData() {
		return data;
	}

	/**
	 * @param data {@link #data}
	 */
	public final void setData(final String data) {
		this.data = data;
	}

	/**
	 * @return {@link #selector}
	 */
	public final String getSelector() {
		return selector;
	}

	/**
	 * @param selector {@link #selector}
	 */
	public final void setSelector(final String selector) {
		this.selector = selector;
	}

	/**
	 * @return {@link #fileName}
	 */
	public final String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName {@link #fileName}
	 */
	public final void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return {@link #errorMessage}
	 */
	public final String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage {@link #errorMessage}
	 */
	public final void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
