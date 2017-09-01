package org.restcomm.connect.rvd.model.steps.dial;

import java.util.List;

import org.restcomm.connect.rvd.model.project.BaseStep;

public class DialStep extends BaseStep {

    protected List<BaseDialNoun> dialNouns;
    protected String action;
    protected String method;
    protected Integer timeout;
    protected Integer timeLimit;
    protected String callerId;
    protected String nextModule;
    protected Boolean record;

}
