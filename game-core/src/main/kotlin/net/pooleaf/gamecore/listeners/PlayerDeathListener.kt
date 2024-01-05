package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerDeathEvent
import net.pooleaf.gamecore.killstreak.KillStreak
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeathListener : Listener {


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDeathDamage(event: PlayerDamageEvent) {
        if (event.isCancelled) return

        val player = event.player

        // 죽을 만큼 데미지를 받을 경우
        if (player.health - event.entityDamageEvent.finalDamage < 1) {
            // 이벤트 캔슬
            event.isCancelled = true

            // 사망 처리
            handlePlayerDeath(player)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        // 사망 처리
        handlePlayerDeath(event.entity)

        // 사망메시지 비활성화 (즉사 시키는 능력 사용 시 사망메시지가 나오지 않도록 하기 위해)
        event.deathMessage = null

        // 드랍템 삭제
        event.drops.clear()
    }

    /**
     * 플레이어 사망 처리
     */
    fun handlePlayerDeath(player: Player) {
        val deadGamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)
        val killerGamePlayer = deadGamePlayer.getKillerGamePlayer()
        var assistGamePlayers = deadGamePlayer.getKillerAssistGamePlayer()

        // 킬 처리
        if (killerGamePlayer != null) {
            // 연속킬
            if (GameCore.gameConfig.useKillStreak) {
                if (killerGamePlayer.lastKillTime?.let { System.currentTimeMillis() - it < GameCore.gameConfig.killStreakValidSeconds * 1000 } == true) {
                    killerGamePlayer.killStreak = killerGamePlayer.killStreak?.getNextKillStreak() ?: KillStreak.DOUBLE
                } else {
                    killerGamePlayer.killStreak = null
                }
            }

            killerGamePlayer.lastKillTime = System.currentTimeMillis()
        }

        // 메시지
        if (killerGamePlayer == null) {
            BukkitBroadcaster.broadcast("§c${deadGamePlayer.displayName} §c님이 죽었습니다.")
        } else {
            // 연속킬 메시지 계산
            val killStreakMessage = if (killerGamePlayer.killStreak != null) {
                "(${killerGamePlayer.killStreak!!.color}${killerGamePlayer.killStreak!!.text})"
            } else {
                ""
            }

            BukkitBroadcaster.broadcast("§c${killerGamePlayer.displayName} §c님이 §c${deadGamePlayer.displayName} §c님을 죽였습니다. ${killStreakMessage}")
        }

        // 이벤트
        Bukkit.getPluginManager().callEvent(GamePlayerDeathEvent(deadGamePlayer, killerGamePlayer, assistGamePlayers))
    }

}