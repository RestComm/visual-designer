package org.restcomm.connect.rvd.interpreter;

import org.restcomm.connect.rvd.exceptions.InterpreterException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface DialNounBehavior {
    public abstract RcmlNoun render(Interpreter interpreter) throws InterpreterException;
}
