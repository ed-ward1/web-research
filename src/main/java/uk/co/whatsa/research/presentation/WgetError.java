package uk.co.whatsa.research.presentation;

/**
 * The exit values of a wget process (ordinals are significant).
 */
enum WgetError {
    /** Wget error code. */
    SUCCESS,
    /** Wget error code. */
    GENERIC_ERROR,
    /** Wget error code. */
    PARSE_ERROR,
    /** Wget error code. */
    FILE_IO_ERROR,
    /** Wget error code. */
    NETWORK_FAILURE,
    /** Wget error code. */
    SSL_VERIFICATION_FAILURE,
    /** Wget error code. */
    USERNAME_PASSWORD_AUTHENTICATION_FAILURE,
    /** Wget error code. */
    PROTOCOL_ERRORS,
    /** Wget error code. */
    SERVER_ERROR_RESPONSE;
}
