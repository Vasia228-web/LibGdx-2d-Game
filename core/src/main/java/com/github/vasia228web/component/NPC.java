package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class NPC implements Component {
    public static final ComponentMapper<NPC> MAPPER = ComponentMapper.getFor(NPC.class);

    public String npcId;

    public NPC(String npcId) {
        this.npcId = npcId;
    }
}
