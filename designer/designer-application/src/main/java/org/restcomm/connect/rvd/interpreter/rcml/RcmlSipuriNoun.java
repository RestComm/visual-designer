package org.restcomm.connect.rvd.interpreter.rcml;


public class RcmlSipuriNoun extends Rcml {

    public static class Video {
        public Boolean enable;
        public String overlay;
    }

    public String destination;
    public String statusCallback;
    public Video video;


    public void setDestination(String destination) {
        this.destination = destination;
    }


}
