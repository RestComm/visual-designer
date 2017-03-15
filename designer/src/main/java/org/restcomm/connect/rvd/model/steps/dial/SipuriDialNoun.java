package org.restcomm.connect.rvd.model.steps.dial;

import org.restcomm.connect.rvd.exceptions.InterpreterException;
import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.utils.RvdUtils;

/**
 * @author Orestis Tsakiridis - otsakir@gmail.com
 */
public class SipuriDialNoun extends DialNoun {
    private String destination;
    private String statusCallback;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public RcmlNoun render(Interpreter interpreter) throws InterpreterException {
        RcmlSipuriNoun rcmlNoun = new RcmlSipuriNoun();
        rcmlNoun.setDestination( interpreter.populateVariables(getDestination() ));
        if (!RvdUtils.isEmpty(statusCallback))
            rcmlNoun.statusCallback = statusCallback;
        return rcmlNoun;
    }
}
