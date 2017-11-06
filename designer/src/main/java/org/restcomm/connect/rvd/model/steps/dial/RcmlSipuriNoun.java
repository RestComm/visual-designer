package org.restcomm.connect.rvd.model.steps.dial;


public class RcmlSipuriNoun extends RcmlNoun {

    public static class Video {
        public Boolean enable;
        public String overlay;
        public String resolution;
    }

    String destination;
    String statusCallback;
    Video video;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
