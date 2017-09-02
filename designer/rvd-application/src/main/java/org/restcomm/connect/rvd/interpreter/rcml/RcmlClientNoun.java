package org.restcomm.connect.rvd.interpreter.rcml;


public class RcmlClientNoun extends Rcml {

    public static class Video {
        public Boolean enable;
        public String overlay;
    }

    String destination;
    String url;
    String statusCallback;
    Video video;

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatusCallback() {
        return statusCallback;
    }

    public Video getVideo() {
        return video;
    }

    public void setStatusCallback(String statusCallback) {
        this.statusCallback = statusCallback;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
