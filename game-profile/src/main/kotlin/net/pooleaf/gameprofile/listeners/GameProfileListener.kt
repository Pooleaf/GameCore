package net.pooleaf.gameprofile.listeners

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.gameprofile.GameStatusChecker
import net.pooleaf.gameprofile.guis.GameProfileGui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class GameProfileListener : Listener {

    @EventHandler
    fun onRightClick(event: PlayerInteractEntityEvent) {
        val rightClicked = event.rightClicked
        if (rightClicked !is Player) return
        if (rightClicked.hasMetadata("NPC")) return

        // 게임 시작 시 사용 안함
        if (GameStatusChecker.isGameChannel() && GameStatusChecker.isGameStarted()) return

        val player = event.player

        // 쉬프트 우클릭만
        if (!player.isSneaking) return

        val targetCommonPlayer = CommonSenderModule.getPlayer(rightClicked.uniqueId) ?: return
        BukkitAsyncScope.launch {
            GameProfileGui(targetCommonPlayer).open(player)
        }
    }

}