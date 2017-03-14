package org.restcomm.connect.rvd.model.steps.dial;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class SipuriDialNoun extends DialNoun {
    private String destination;
    private String statusCallback;

    public String getDestination() {
        return destination;
    }

    public String getStatusCallback() {
        return statusCallback;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public RcmlNoun render(Interpreter interpreter) throws InterpreterException {
        RcmlSipuriNoun rcmlNoun = new RcmlSipuriNoun();
        rcmlNoun.setDestination( interpreter.populateVariables(getDestination() ));
        return rcmlNoun;
    }
}
