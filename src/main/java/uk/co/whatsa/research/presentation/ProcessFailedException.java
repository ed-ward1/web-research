package uk.co.whatsa.research.presentation;

import java.util.List;

/**
 * Custom exception thrown when wget fails.
 */
public class ProcessFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/** The error code returned by {@code wget}. */
	private final int errorCode;

	/** Lines from the log file created by wget. */
	private final List<String> errorMessageLines;

	/**
	 * @param errorCode {@link #errorCode}
	 * @param errorMessageLines {@link #errorMessageLines}
	 */
	public ProcessFailedException(final int errorCode, final List<String> errorMessageLines) {
	    super();
		this.errorCode = errorCode;
		this.errorMessageLines = errorMessageLines;
	}

	/**
	 * @return {@link #errorCode}
	 */
	public final int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return {@link #errorMessageLines}
	 */
	public final List<String> getErrorMessageLines() {
		return errorMessageLines;
	}
}