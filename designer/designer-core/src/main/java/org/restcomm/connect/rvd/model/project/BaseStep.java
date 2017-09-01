
package org.restcomm.connect.rvd.model.project;


import org.restcomm.connect.rvd.validation.ValidationErrorItem;

import java.util.List;

public abstract class BaseStep {

    String kind;
    String label;
    String title;
    String name;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ValidationErrorItem> validate(String stepPath, Node parentModule) {
        return null;
    }
}
