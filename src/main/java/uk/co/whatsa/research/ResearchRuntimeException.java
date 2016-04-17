package uk.co.whatsa.research;

/**
 * A {@link RuntimeException} used by the web research application.
 */
public class ResearchRuntimeException extends RuntimeException {

    /** serialVersionUID. */
    private static final long serialVersionUID = 3214451185382550435L;

    /**
     * Constructs a new runtime exception with null as its detail
     * message.
     */
    public ResearchRuntimeException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail
     * message.
     * 
     * @param message the detail message. The detail message is saved
     *            for later retrieval by the
     *            {@link Throwable#getMessage()} method.
     */
    public ResearchRuntimeException(final String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified cause and
     * a detail message of (cause==null ? null : cause.toString())
     * (which typically contains the class and detail message of
     * cause). This constructor is useful for runtime exceptions that
     * are little more than wrappers for other throwables.
     * 
     * @param cause the cause (which is saved for later retrieval by
     *            the {@link Throwable#getCause()} method). (A null
     *            value is permitted, and indicates that the cause is
     *            nonexistent or unknown.)
     */
    public ResearchRuntimeException(final Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param message the detail message (which is saved for later
     *            retrieval by the {@link Throwable#getMessage()}
     *            method).
     * @param cause the cause (which is saved for later retrieval by
     *            the {@link Throwable#getCause()} method). (A null value is
     *            permitted, and indicates that the cause is
     *            nonexistent or unknown.)
     */
    public ResearchRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     * 
     * @param message the detail message
     * @param cause the cause. (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled
     *            or disabled
     * @param writableStakTrace whether or not the stack trace should
     *            be writable
     */
    public ResearchRuntimeException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStakTrace) {
        super(message, cause, enableSuppression, writableStakTrace);
    }
}
