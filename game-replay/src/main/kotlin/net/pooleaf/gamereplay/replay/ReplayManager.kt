package net.pooleaf.gamereplay.replay

import net.pooleaf.core.modules.support.common.manager.AbstractEhcacheManager
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.entity.Player
import java.util.*

class ReplayManager: AbstractEhcacheManager<UUID, Replay>() {

    /**
     * 해당 리플레이를 시청 중인 플레이어를 반환합니다.
     */
    fun getViewers(replay: Replay): List<Player> {
        return GameReplayApi.unsafe.replayPlayerManager.values()
            .filter { it.replay == replay }
            .map { it.viewer }
    }

    /**
     * 시청 중이 아닌 리플레이를 제거합니다.
     */
    fun removeNotWatchingReplays() {
        keys().filter { getViewers(get(it)).isEmpty() }
            .forEach { remove(it) }
    }

}