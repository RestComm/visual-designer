package org.restcomm.connect.rvd.exceptions;

/**
 * Thrown for cases when the user has authenticated but is not authorized to access a specific resource
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ForbiddenResourceException extends AuthorizationException {
    public ForbiddenResourceException() {
    }

    public ForbiddenResourceException(String message) {
        super(message);
    }
}
