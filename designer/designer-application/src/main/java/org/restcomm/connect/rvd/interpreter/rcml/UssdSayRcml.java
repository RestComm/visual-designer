package org.restcomm.connect.rvd.interpreter.rcml;


public class UssdSayRcml extends Rcml {
    String language;
    public String text;

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }
}
