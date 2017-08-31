package org.restcomm.connect.rvd.model.project;

import java.util.ArrayList;

public abstract class RvdProject {

    static final String MODULE_PREFIX_DEFAULT = "module"; // prefix that will be used when creating modules(nodes). Will result in module names like module1, module2 etc.
    static final String STEP_PREFIX_DEFAULT = "step"; // prefix that will be used when creating steps with modules like step1, step2 etc.

    String name;
    ProjectState state;

    public RvdProject(String name, ProjectState projectState) {
        this.name = name;
        this.state = projectState;
    }

    // not public since it should not be used directly
    RvdProject(String name) {
        this.name = name;
        state = new ProjectState();
        state.lastStepId = 0;
        state.lastNodeId = 0;
        state.nodes = new ArrayList<Node>();
        state.header = new StateHeader();
    }

    public RvdProject(String name, String owner, String version) {
        this(name);
        state.header.owner = owner;
        state.header.version = version;
    }


    public Node newModule(String kind) {
        Integer moduleId = ++state.lastNodeId;
        String moduleName = MODULE_PREFIX_DEFAULT + moduleId;
        return newModule(kind, moduleName);
    }

    public Node newModule(String kind, String moduleName) {
        Node newNode = new Node().setName(moduleName).setLabel(moduleName).setKind(kind);
        state.nodes.add(newNode);
        // if this is the first module added (no id has been generated), inform the header too
        if (state.lastNodeId == 0)
            state.header.startNodeName = moduleName;
        return newNode;
    }

    /**
     * Adds the newStep to a module step list after it generates a nice unique name for  it
     * @param newStep
     */
    public <T extends BaseStep> T addStep(T newStep, String parentModule) {
        Integer stepId = ++state.lastStepId;
        String stepName = STEP_PREFIX_DEFAULT + stepId;
        newStep.name = stepName;
        getModule(parentModule).getSteps().add(newStep);
        return newStep;
    }

    private Node getModule(String searchedName) {
        if (state.nodes != null) {
            for (int i = 0; i < state.nodes.size(); i++) {
                if ("searchedName".equals(state.nodes.get(i).getName())) {
                    return state.nodes.get(i);
                }
            }
        }
        return null;
    }

    public abstract boolean supportsWavs();

    public String getName() {
        return name;
    }

    public ProjectState getState() {
        return state;
    }



}
