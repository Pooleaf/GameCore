package net.pooleaf.gamecore.listeners

import com.cryptomorin.xseries.XSound
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.supply.SupplyGetEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent

class SupplyListener: Listener {

    @EventHandler
    fun onInteractSupply(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (!GameCore.game.isGameStarted) return

        // 플레이어 체크
        val player = event.player
        val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)
        if (!gamePlayer.isPlaying()) return

        // 보급품 우클릭 시 획득
        val block = event.clickedBlock
        val location = block.location

        val supplyBlock = GameCore.unsafe.supplyManager.getCreatedSupply(location)
        supplyBlock?.let { supplyBlock ->
            event.isCancelled = true

            if (supplyBlock.usedBy == null) {
                // 이벤트
                val supplyGetEvent = SupplyGetEvent(gamePlayer, supplyBlock)
                if (supplyGetEvent.isCancelled) return

                // 아이템 지급
                supplyBlock.supply.items.forEach { player.inventory.addItem(it) }
                supplyBlock.usedBy = gamePlayer

                gamePlayer.sendMessageSafely("§e보급품을 획득했습니다.")
                gamePlayer.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)
            } else {
                gamePlayer.sendWarningSafely("이미 누군가가 발견한 보급품입니다.")
            }
        }
    }

    @EventHandler
    fun onSupplyBreak(event: BlockBreakEvent) {
        if (!GameCore.game.isGameStarted) return

        // 보급품은 부실 수 없음
        val location = event.block.location
        if (GameCore.unsafe.supplyManager.getCreatedSupply(location) != null) {
            event.isCancelled = true
        }
    }

}