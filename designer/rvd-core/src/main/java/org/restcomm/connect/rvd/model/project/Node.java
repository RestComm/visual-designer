package org.restcomm.connect.rvd.model.project;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String name;
    private String label;
    private String kind;
    private List<BaseStep> steps;

    public Node() {
        // TODO Auto-generated constructor stub
    }

    public static Node createDefault(String kind, String name, String label) {
        Node node = new Node();
        node.setName(name);
        node.setLabel(label);
        node.setKind(kind);
        List<BaseStep> steps = new ArrayList<BaseStep>();
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

    public List<BaseStep> getSteps() {
        return steps;
    }

    public Node setSteps(List<BaseStep> steps) {
        this.steps = steps;
        return this;
    }
}
