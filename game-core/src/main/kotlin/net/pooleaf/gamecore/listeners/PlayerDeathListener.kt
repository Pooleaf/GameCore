package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerDeathEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeathListener : Listener {


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDeathDamage(event: PlayerDamageEvent) {
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
    }

    /**
     * 플레이어 사망 처리
     */
    fun handlePlayerDeath(player: Player) {
        val deadGamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)
        val killerGamePlayer = deadGamePlayer.getKillerGamePlayer()
        var assistGamePlayers = deadGamePlayer.getKillerAssistGamePlayer()

        // 메시지
        if (killerGamePlayer == null) {
            BukkitBroadcaster.broadcast("§c${deadGamePlayer.displayName} §c님이 죽었습니다.")
        } else {
            BukkitBroadcaster.broadcast("§c${killerGamePlayer.displayName} §c님이 §c${deadGamePlayer.displayName} §c님을 죽였습니다.")
        }

        // 이벤트
        Bukkit.getPluginManager().callEvent(GamePlayerDeathEvent(deadGamePlayer, killerGamePlayer, assistGamePlayers))
    }

}