package org.restcomm.connect.rvd.model.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node {

    private String name;
    private String label;
    private String kind;
    private List<Step> steps;

    public Node() {
        // TODO Auto-generated constructor stub
    }

    public static Node createDefault(String kind, String name, String label) {
        Node node = new Node();
        node.setName(name);
        node.setLabel(label);
        node.setKind(kind);
        List<Step> steps = new ArrayList<Step>();
        node.setSteps(steps);

        return node;
    }

    public String getName() {
        return name;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Node setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getKind() {
        return kind;
    }

    public Node setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Node setSteps(List<Step> steps) {
        this.steps = steps;
        return this;
    }

    /**
     * Returns a step named stepName or null if nothing is matched
     *
     * @param stepName
     * @return step or null
     */
    public Step getStepByName(String stepName) {
        if (stepName == null)
            throw new IllegalArgumentException("stepName shouldn't be null");
        Iterator<Step> i = steps.iterator();
        Step step;
        while ( i.hasNext() ) {
            step = i.next();
            if (stepName.equals(step.getName()) ) {
                return step;
            }
        }
        return null;

    }

    public List<String> getStepNames() {
        List<String> names = new ArrayList<String>();
        Iterator<Step> i = steps.iterator();
        while (i.hasNext()) {
            names.add(i.next().getName());
        }
        return names;
    }
}
