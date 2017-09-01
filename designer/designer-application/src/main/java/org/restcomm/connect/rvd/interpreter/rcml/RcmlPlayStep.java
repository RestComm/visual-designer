package org.restcomm.connect.rvd.interpreter.rcml;


public class RcmlPlayStep extends Rcml {
    private String wavurl;
    private Integer loop;

    public String getWavurl() {
        return wavurl;
    }

    public void setWavurl(String wavurl) {
        this.wavurl = wavurl;
    }

    public Integer getLoop() {
        return loop;
    }

    public void setLoop(Integer loop) {
        this.loop = loop;
    }
}
