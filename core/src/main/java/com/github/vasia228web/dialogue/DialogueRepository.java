package com.github.vasia228web.dialogue;

import java.util.HashMap;
import java.util.Map;

public class DialogueRepository {


    private final Map<String, DialogueData> dialogues;

    public DialogueRepository() {
        this.dialogues = new HashMap<>();

        dialogues.put("woman", new DialogueData(
            "woman",
            "Woman",
            "Hello! Nice to see you.",
            "woman_portrait"
        ));

    }

    public DialogueData getDialogue(String npcId) {
        return dialogues.get(npcId);
    }

}
