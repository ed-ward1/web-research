package uk.co.whatsa.research.presentation;

/**
 * Since the page is captured asynchronously (in a separate thread)
 * this class encapsulates the results of the page capture process.
 */
public class CaptureWebPageResult {
    /**
     * The exception should the page capture process fail.
     */
    private ProcessFailedException processFailedException;

    /**
     * @return {@link #processFailedException}
     */
    public final ProcessFailedException getProcessFailedException() {
        return processFailedException;
    }

    /**
     * @param processFailedException {@link #processFailedException}
     */
    public final void setProcessFailedException(final ProcessFailedException processFailedException) {
        this.processFailedException = processFailedException;
    }
}
