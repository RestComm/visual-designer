package org.restcomm.connect.rvd.model.project;

public class SmsProject extends RvdProject {

    public SmsProject(String name, ProjectState projectState) {
        super(name, projectState);
    }

    public SmsProject(String name, String owner, String version) {
        super(name, owner, version);
        this.getState().header.projectKind = "sms";
    }

    public Node newModule() {
        return super.newModule("sms");
    }

    public Node newModule(String name) {
        return super.newModule("sms", name);
    }

    @Override
    public boolean supportsWavs() {
        return false;
    }

}
