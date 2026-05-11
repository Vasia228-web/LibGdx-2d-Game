package com.github.vasia228web.dialogue;

public class DialogueData {

    private final String npcId;
    private final String speakerName;
    private final String text;
    private final String portraitName;

    public DialogueData(String npcId, String speakerName, String text, String portraitName) {
        this.npcId = npcId;
        this.speakerName = speakerName;
        this.text = text;
        this.portraitName = portraitName;
    }

    public String getNpcId() {
        return npcId;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public String getText() {
        return text;
    }

    public String getPortraitName() {
        return portraitName;
    }

}
