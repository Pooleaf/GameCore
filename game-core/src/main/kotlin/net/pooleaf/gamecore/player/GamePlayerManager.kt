package net.pooleaf.gamecore.player

import net.pooleaf.core.modules.support.common.manager.AbstractSyncManager
import java.util.*

open class GamePlayerManager<T: GamePlayer>(
    val gamePlayerFactory: GamePlayerFactory<T>
): AbstractSyncManager<UUID, T>() {

    override fun remove(key: UUID?): Boolean {
        // 팀에서도 삭제
        get(key)?.let {
            it.team?.removePlayer(it)
            super.remove(key)

            return true
        }

        return false
    }

    /**
     * [name]으로 [T]를 찾아 반환합니다.
     */
    fun getByName(name: String): T? {
        return values().firstOrNull { it.name.equals(name, true) }
    }

    /**
     * 온라인 [T]의 목록을 반환합니다.
     */
    fun getOnlinePlayers(): List<T> {
        return values().filter { it.isOnline }
            .toList()
    }

    /**
     * 오프라인 [T]를 포함한 게임에 참여한 [T]의 목록을 반환합니다.
     * 탈락하거나 관전 중인 플레이어를 포함합니다.
     */
    fun getJoinedPlayers(): List<T> {
        return values().filter { it.isJoined }
            .toList()
    }

    /**
     * 참여 중인 온라인 [T]의 [List]를 반환합니다.
     * 탈락하거나 관전 중인 플레이어를 포함합니다.
     */
    fun getOnlineJoinedPlayers(): List<T> {
        return values().filter { it.isJoined && it.isOnline }
            .toList()
    }

    /**
     * 오프라인 [T]를 포함한 게임에서 탈락하지 않고 플레이 중인 [T]의 목록을 반환합니다.
     */
    fun getPlayingPlayers(): List<T> {
        return values().filter { it.isPlaying() }
            .toList()
    }

    /**
     * 게임에서 탈락하지 않고 플레이 중인 온라인 [T]의 목록을 반환합니다.
     */
    fun getOnlinePlayingPlayers(): List<T> {
        return values().filter { it.isPlaying() && it.isOnline }
            .toList()
    }

    /**
    * 관전 중인 [T]를 반환합니다.
    * 관전 중인 [T]는 퇴장 시 관전이 해제되므로 온라인 상태인 [T]만 반환됩니다.
    */
    fun getOnlineSpectators(): List<T> {
        return values().filter { it.isSpectator && it.isOnline }
    }

}