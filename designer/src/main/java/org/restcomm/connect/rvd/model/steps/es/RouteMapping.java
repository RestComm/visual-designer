package org.restcomm.connect.rvd.model.steps.es;

public class RouteMapping {
    public RouteMapping(String value, String next) {
        this.value = value;
        this.next = next;
    }

    private String value;
    private String next;
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getNext() {
        return next;
    }
    public void setNext(String next) {
        this.next = next;
    }
}
