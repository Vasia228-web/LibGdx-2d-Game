    package com.github.vasia228web.dialogue;

    import com.badlogic.gdx.Gdx;

    public class DialogueSystem {

        private DialogueData currentDialogue;
        private final DialogueRepository dialogueRepository;
        boolean isActive;

        public DialogueSystem(DialogueRepository dialogueRepository) {

            this.dialogueRepository = dialogueRepository;
            this.isActive = false;
            this.currentDialogue = null;

        }

        public void startDialog(String npcId) {

            DialogueData dialogueData = dialogueRepository.getDialogue(npcId);

            if (dialogueData == null) {
                dialogueData = new DialogueData(
                    npcId,
                    "Unknown",
                    "...",
                    null
                );
            } else {
                Gdx.app.log("DIALOGUE", "Dialogue found: " + dialogueData.getText());
            }

            this.currentDialogue = dialogueData;
            this.isActive = true;

        }

        public void closeDialog() {
            this.isActive = false;
            this.currentDialogue = null;
        }


        public boolean isActive() {
            return isActive;
        }

        public DialogueData getCurrentDialogue() {
            return currentDialogue;
        }

        public String getText() {
            if (currentDialogue == null) return null;
            return currentDialogue.getText();
        }

        public String getPortraitName() {
            if (currentDialogue == null) return null;
            return currentDialogue.getPortraitName();
        }

        public String getSpeakerName() {
            if (currentDialogue == null) return null;
            return currentDialogue.getSpeakerName();
        }


    }
