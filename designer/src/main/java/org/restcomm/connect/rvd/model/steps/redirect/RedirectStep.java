package org.restcomm.connect.rvd.model.steps.redirect;

import org.restcomm.connect.rvd.interpreter.Interpreter;
import org.restcomm.connect.rvd.jsonvalidation.ValidationErrorItem;
import org.restcomm.connect.rvd.model.project.Node;
import org.restcomm.connect.rvd.model.project.Step;
import org.restcomm.connect.rvd.utils.RvdUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedirectStep extends Step {
    String url;
    String method;
    String next; // module in current app to redirect to

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public RcmlRedirectStep render(Interpreter interpreter, String containerModule) {
        RcmlRedirectStep rcmlStep = new RcmlRedirectStep();
        if (!RvdUtils.isEmpty(url))
            rcmlStep.setUrl(interpreter.populateVariables(url));
        else
        if (!RvdUtils.isEmpty(next)) {
            Map<String, String> pairs = new HashMap<String, String>();
            pairs.put("target", next);
            rcmlStep.setUrl(interpreter.buildAction(pairs));
        }
        if ( !RvdUtils.isEmpty(method) )
            rcmlStep.setMethod(getMethod());

        return rcmlStep;
    }

    /**
     * Checks for semantic validation error in the state object and returns them as ErrorItems. If no error
     * is detected an empty list is returned
     *
     * @return a list of ValidationErrorItem objects or an empty list
     * @param stepPath
     * @param module
     */
    @Override
    public List<ValidationErrorItem> validate(String stepPath, Node module) {
        List<ValidationErrorItem> errorItems = new ArrayList<ValidationErrorItem>();
        // either 'url' or 'next' should contain sth so that a target can be determined
        if (RvdUtils.isEmpty(url) && RvdUtils.isEmpty((next))) {
            errorItems.add(new ValidationErrorItem("error", "No target could be determined. A module or a URL should be provided", stepPath));
        }
        return errorItems;
    }



}
