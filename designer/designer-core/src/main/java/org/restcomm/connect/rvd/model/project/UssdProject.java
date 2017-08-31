package org.restcomm.connect.rvd.model.project;

public class UssdProject extends RvdProject {

    public UssdProject(String name, ProjectState projectState) {
        super(name, projectState);
    }

    public Node newModule() {
        return super.newModule("ussd");
    }

    @Override
    public boolean supportsWavs() {
        return false;
    }

}
