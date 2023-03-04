package net.pooleaf.gamereplay.replay.virtual.player

import net.citizensnpcs.api.CitizensAPI
import net.pooleaf.core.modules.support.common.manager.AbstractManager
import java.util.*

/**
 * 가상 플레이어 관리자
 * UUID, VirtualPlayer
 */
class VirtualPlayerManager : AbstractManager<UUID, VirtualPlayer>() {

    val npcRegistry = CitizensAPI.createAnonymousNPCRegistry(VirtualNpcDataStore())

}