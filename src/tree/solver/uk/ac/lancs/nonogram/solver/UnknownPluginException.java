// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.solver;

import uk.ac.lancs.nonogram.plugin.PluginException;

/**
 * No matching plug-in could be found.
 * 
 * @author simpsons
 */
public class UnknownPluginException extends PluginException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with {@code null} as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause(java.lang.Throwable)}.
     */
    public UnknownPluginException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause(java.lang.Throwable)}.
     * 
     * @param message the detail message
     */
    public UnknownPluginException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause, and with
     * {@code null} as its detail message.
     * 
     * @param cause the cause; {@code null} is permitted if there is no
     * cause
     */
    public UnknownPluginException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * 
     * @param message the detail message
     * 
     * @param cause the cause; {@code null} is permitted if there is no
     * cause
     */
    public UnknownPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message,
     * cause, suppression enabled or disabled, and writable stack trace
     * enabled or disabled.
     * 
     * @param message the detail message
     * 
     * @param cause the cause; {@code null} is permitted if there is no
     * cause
     * 
     * @param enableSuppression whether suppression is enabled
     * 
     * @param writableStackTrace whether the stack trace should be
     * writable
     */
    public UnknownPluginException(String message, Throwable cause,
                                  boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
