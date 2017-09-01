package org.restcomm.connect.rvd.interpreter.rcml;

import org.restcomm.connect.rvd.model.rcml.RcmlStep;

public class UssdSayRcml extends RcmlStep {
    String language;
    String text;

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
