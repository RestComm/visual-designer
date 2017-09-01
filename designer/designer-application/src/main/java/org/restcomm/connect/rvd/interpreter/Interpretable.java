package org.restcomm.connect.rvd.interpreter;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.rcml.Rcml;

/**
 * Sth that can be rendered to RCML by the interpreter
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface Interpretable {
    // TODO - this used to return RcmlNoun. Make sure it works right
    Rcml render(Interpreter interpreter) throws InterpreterException;
}
