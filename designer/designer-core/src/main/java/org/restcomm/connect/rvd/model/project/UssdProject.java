package org.restcomm.connect.rvd.model.project;

public class UssdProject extends RvdProject {

    public UssdProject(String name, ProjectState projectState) {
        super(name, projectState);
    }

    public UssdProject(String name, String owner, String version) {
        super(name, owner, version);
        this.getState().header.projectKind = "ussd";
    }

    public Node newModule() {
        return super.newModule("ussd");
    }

    public Node newModule(String name) {
        return super.newModule("ussd", name);
    }

    @Override
    public boolean supportsWavs() {
        return false;
    }

}
