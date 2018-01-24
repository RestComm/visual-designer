package org.restcomm.connect.rvd.model.project;

import org.restcomm.connect.rvd.RvdConfiguration;
import org.restcomm.connect.rvd.exceptions.IncompatibleProjectVersion;
import org.restcomm.connect.rvd.exceptions.RvdException;
import org.restcomm.connect.rvd.exceptions.project.InvalidProjectKind;
import org.restcomm.connect.rvd.model.StepJsonDeserializer;
import org.restcomm.connect.rvd.model.StepJsonSerializer;
import org.restcomm.connect.rvd.storage.exceptions.BadProjectHeader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

    public RvdProject(String name) {
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
        Node newNode = new Node().setName(moduleName).setLabel(moduleName).setKind(kind);
        state.nodes.add(newNode);
        return newNode;
    }

    /**
     * Adds the newStep to a module step list after it generates a nice unique name for  it
     * @param newStep
     */
    public void addStep(Step newStep, String parentModule) {
        Integer stepId = ++state.lastStepId;
        String stepName = STEP_PREFIX_DEFAULT + stepId;
        newStep.name = stepName;
        getModule(parentModule).getSteps().add(newStep);
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

    public static RvdProject fromState(String name, ProjectState state) throws RvdException {
        String kind = state.getHeader().getProjectKind();
        RvdProject project = null;
        if ( "voice".equals(kind) ) {
            project = new VoiceProject(name, state);
        } else
        if ( "sms".equals(kind) ) {
            project = new SmsProject(name, state);
        } else
        if ( "ussd".equals(kind) ) {
            project = new UssdProject(name, state);
        } else {
            throw new InvalidProjectKind("Can't create project " + name +". Unknown project kind: " + kind);
        }
        return project;
    }

    public static ProjectState toModel(String projectJson) throws RvdException {
        Gson gson = new GsonBuilder()
        .registerTypeAdapter(Step.class, new StepJsonDeserializer())
        .registerTypeAdapter(Step.class, new StepJsonSerializer())
        .create();

        // Check header first
        JsonParser parser = new JsonParser();
        JsonElement header_element = parser.parse(projectJson).getAsJsonObject().get("header");
        if ( header_element == null )
            throw new BadProjectHeader("No header found. This is probably an old project");

        StateHeader header = gson.fromJson(header_element, StateHeader.class);
        if ( ! header.getVersion().equals(RvdConfiguration.RVD_PROJECT_VERSION) )
                throw new IncompatibleProjectVersion("Error loading project. Project version: " + header.getVersion() + " - RVD project version: " + RvdConfiguration.RVD_PROJECT_VERSION );
        // Looks like a good project. Make a ProjectState object out of it
        ProjectState projectState = gson.fromJson(projectJson, ProjectState.class);
        return projectState;
    }

    public abstract boolean supportsWavs();

    public String getName() {
        return name;
    }

    public ProjectState getState() {
        return state;
    }



}
