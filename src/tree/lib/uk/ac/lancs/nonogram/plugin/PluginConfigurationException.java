// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.plugin;

/**
 * A plug-in could not be configured with supplied parameters. The
 * parameters are normally passed in the detail message.
 * 
 * @author simpsons
 */
public class PluginConfigurationException extends PluginException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with {@code null} as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause(Throwable)}.
     */
    public PluginConfigurationException() {}

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
    public PluginConfigurationException(String message, Throwable cause,
                                        boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
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
    public PluginConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause(Throwable)}.
     * 
     * @param message the detail message
     */
    public PluginConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause, and with
     * {@code null} as its detail message.
     * 
     * @param cause the cause; {@code null} is permitted if there is no
     * cause
     */
    public PluginConfigurationException(Throwable cause) {
        super(cause);
    }
}
