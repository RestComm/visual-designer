package org.restcomm.connect.rvd.model;

import org.restcomm.connect.rvd.project.ProjectKind;

/**
 * A model class to build a response when creating a project
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class ProjectCreatedDto {

    String name; // this is the friendly name, not the id
    String sid;  // same as application ID
    ProjectKind kind; // voice|sms|ussd

    public ProjectCreatedDto(String name, String sid, ProjectKind kind) {
        this.name = name;
        this.sid = sid;
        this.kind = kind;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setKind(ProjectKind kind) {
        this.kind = kind;
    }
}
