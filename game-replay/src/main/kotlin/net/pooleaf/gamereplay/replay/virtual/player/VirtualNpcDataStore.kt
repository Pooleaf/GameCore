package net.pooleaf.gamereplay.replay.virtual.player

import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.npc.NPCDataStore
import net.citizensnpcs.api.npc.NPCRegistry

class VirtualNpcDataStore : NPCDataStore {

    var lastNpcId: Int = 0


    override fun clearData(p0: NPC?) {
    }

    override fun createUniqueNPCId(p0: NPCRegistry?): Int {
        return ++lastNpcId
    }

    override fun loadInto(p0: NPCRegistry?) {
    }

    override fun saveToDisk() {
    }

    override fun saveToDiskImmediate() {
    }

    override fun store(p0: NPC?) {
    }

    override fun storeAll(p0: NPCRegistry?) {
    }

    override fun reloadFromSource() {
    }

}