package org.eclipse.core.runtime;

/**
 * This exception is thrown to blow out of a long-running method
 * when the user cancels it.
 * <p>
 * This class can be used without OSGi running.
 * </p><p>
 * This class is not intended to be subclassed by clients but
 * may be instantiated.
 * </p>
 */
public final class OperationCanceledException extends RuntimeException {
    /**
     * All serializable objects should have a stable serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception.
     */
    public OperationCanceledException() {
        super();
    }

    /**
     * Creates a new exception with the given message.
     *
     * @param message the message for the exception
     */
    public OperationCanceledException(String message) {
        super(message);
    }
}
