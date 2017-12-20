package org.restcomm.connect.rvd.exceptions;

/**
 * Thrown when something went wrong with application bootstrapping what will affect the normal application operation.
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class BootstrappingException extends RvdException {
    public BootstrappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
