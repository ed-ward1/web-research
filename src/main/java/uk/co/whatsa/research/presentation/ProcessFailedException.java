package uk.co.whatsa.research.presentation;

import java.util.List;

public class ProcessFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private int errorCode;

	private List<String> errorMessageLines;

	public ProcessFailedException(final int errorCode, final List<String> errorMessageLines) {
		this.errorCode = errorCode;
		this.errorMessageLines = errorMessageLines;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public List<String> getErrorMessageLines() {
		return errorMessageLines;
	}
}
