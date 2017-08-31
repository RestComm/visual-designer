package org.restcomm.connect.rvd.model.project;

//import org.restcomm.connect.rvd.RvdConfiguration;

public class VoiceProject extends RvdProject{

    public VoiceProject(String name, String owner, String version) {
        super(name, owner, version);
        this.getState().header.projectKind = "voice";
    }

    public VoiceProject(String name, ProjectState projectState) {
        super(name, projectState);
    }

    public Node newModule() {
        return super.newModule("voice");
    }

    public Node newModule(String name) {
        return super.newModule("voice", name);
    }

    @Override
    public boolean supportsWavs() {
        return true;
    }

}
