package net.pooleaf.gamecore.player

import kotlinx.coroutines.Job
import net.pooleaf.core.modules.support.bukkit.player.AbstractBukkitPlayer
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.killstreak.KillStreak
import net.pooleaf.gamecore.team.Team
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class GamePlayer(uuid: UUID) : AbstractBukkitPlayer(uuid) {

    // 게임 참여 여부
    var isJoined = false
        internal set

    // 탈락 여부
    var isDefeated = false
        internal set

    // 관전 여부
    var isSpectator = false
        internal set

    // 시작 아이템 지급 여부
    var isReceiveStartItems = false
        internal set

    // 팀
    var team: Team? = null
        internal set

    // 재접속 Job
    var reconnectJob: Job? = null
        internal set

    // 다른 플레이어가 이 플레이어를 마지막으로 때린 시간
    val lastDamagers = ConcurrentHashMap<GamePlayer, Long>() // GamePlayer, Millis

    // 마지막에 이 플레이어를 때린 플레이어와 시간
    val lastDamager: Pair<GamePlayer, Long>? // GamePlayer, Millis
        get() = lastDamagers.toList().maxByOrNull { (key, value) -> value }

    // 연속 킬
    var killStreak: KillStreak? = null

    // 마지막 킬 시간
    var lastKillTime: Long? = null


    /**
     * 게임 플레이 중 여부를 반환합니다.
     */
    fun isPlaying(): Boolean {
        return isJoined && !isDefeated && !isSpectator
    }

    /**
     * 플레이어 정보를 초기화합니다.
     */
    fun init() {
        GameCore.unsafe.playerService.initPlayer(this)
    }

    /**
     * 플레이어 게임 상태를 리셋합니다.
     * 온라인 플레이어만 사용 가능합니다.
     */
    suspend fun reset() {
        GameCore.unsafe.playerService.resetPlayer(this)
    }

    /**
     * 플레이어의 관전 모드를 활성화합니다.
     */
    suspend fun enableSpectatorMode() {
        GameCore.unsafe.playerService.enableSpectatorMode(this)
    }

    /**
     * 플레이어의 관전 모드를 비활성화합니다.
     */
    suspend fun disableSpectatorMode() {
        GameCore.unsafe.playerService.disableSpectatorMode(this)
    }

    /**
     * 플레이어를 탈락시킵니다.
     */
    suspend fun defeat() {
        GameCore.unsafe.playerService.defeatPlayer(this)
    }

    /**
     * 플레이어에게 시작 아이템을 지급합니다.
     * 온라인 플레이어에게만 사용할 수 있습니다.
     */
    fun giveStartItem() {
        GameCore.unsafe.startItemService.giveStartItem(this)
    }

    /**
     * 플레이어를 죽인 플레이어를 반환합니다.
     * 없으면 null을 반환합니다.
     */
    fun getKillerGamePlayer(): GamePlayer? {
        if (lastDamager?.let { System.currentTimeMillis() - it.second <= GameCore.gameConfig.killValidSeconds * 1000L } == true) return lastDamager!!.first
        return null
    }

    /**
     * 플레이어를 죽이는데 도움을 준 플레이어를 반환합니다.
     * 없으면 null을 반환합니다.
     */
    fun getKillerAssistGamePlayer(): List<GamePlayer>? {
        var assistGamePlayers = getKillerGamePlayer()?.team?.players?.filter { teamPlayer ->
            teamPlayer != getKillerGamePlayer() && lastDamagers.get(teamPlayer)?.let { lastHitTime -> System.currentTimeMillis() - lastHitTime <= GameCore.gameConfig.assistValidSeconds * 1000 } == true
        }
        if (assistGamePlayers?.isEmpty() == true) {
            assistGamePlayers = null
        }

        return assistGamePlayers
    }

}