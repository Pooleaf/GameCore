package net.pooleaf.gamecore

import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster

object GameBroadcaster {

    fun broadcastWaitingActionBar(currentJoinedCount: Int, startPlayerCount: Int, ) {
        BukkitBroadcaster.broadcastActionBarForever("§e다른 플레이어를 기다리는 중입니다. §f($currentJoinedCount/$startPlayerCount)")
    }

}