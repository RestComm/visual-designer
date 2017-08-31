package org.restcomm.connect.rvd.model.steps.say;

import org.restcomm.connect.rvd.model.project.BaseStep;

public class SayStep extends BaseStep {

    private String phrase;
    private String voice;
    private String language;
    private Integer loop;

    /*
    public static SayStep createDefault(String name, String phrase) {
        SayStep step = new SayStep();
        step.setName(name);
        step.setLabel("say");
        step.setKind("say");
        step.setTitle("say");
        step.setPhrase(phrase);

        return step;
    }*/

    public SayStep(String phrase) {
        setLabel("say");
        setKind("say");
        setTitle("say");
        this.phrase = phrase;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getLoop() {
        return loop;
    }

    public void setLoop(Integer loop) {
        this.loop = loop;
    }

}
