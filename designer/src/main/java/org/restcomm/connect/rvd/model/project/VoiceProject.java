package org.restcomm.connect.rvd.model.project;

//import org.restcomm.connect.rvd.RvdConfiguration;

public class VoiceProject extends RvdProject{

    public VoiceProject(String name, ProjectState projectState) {
        super(name, projectState);
    }

    public Node newModule() {
        return super.newModule("voice");
    }

    @Override
    public boolean supportsWavs() {
        return true;
    }

}
