package org.restcomm.connect.rvd.model.project;

public class SmsProject extends RvdProject {

    public SmsProject(String name, ProjectState projectState) {
        super(name, projectState);
    }

    public Node newModule() {
        return super.newModule("sms");
    }

    @Override
    public boolean supportsWavs() {
        return false;
    }

}
