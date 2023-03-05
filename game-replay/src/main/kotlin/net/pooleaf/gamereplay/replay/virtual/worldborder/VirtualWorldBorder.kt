package net.pooleaf.gamereplay.replay.virtual.worldborder

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.game.GameWorldBorderChangeData
import net.pooleaf.gamereplay.replay.virtual.VirtualHistory
import org.bukkit.entity.Player

class VirtualWorldBorder : VirtualHistory() {

    fun timeMachine(tick: Long, viewer: Player) {
        getLastData(GameWorldBorderChangeData::class.java, tick)?.let { data ->
            val playerHandler = GameReplayApi.unsafe.recordDataManager.get(data.javaClass) ?: return
            playerHandler.onPlay(data, viewer)
        }
    }

}