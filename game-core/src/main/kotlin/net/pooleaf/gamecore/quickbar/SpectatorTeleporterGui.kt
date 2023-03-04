package net.pooleaf.gamecore.quickbar

import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.pageable.LargePageableGui
import net.pooleaf.core.modules.support.bukkit.messager.sendWarningSafely
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SpectatorTeleporterGui: LargePageableGui("관전할 플레이어를 선택하세요.") {

    override fun onUpdate() {
        clear()

        GameCore.unsafe.playerManager.getOnlinePlayingPlayers().forEach { gamePlayer ->
            addItem(object : InventoryIcon() {
                val gamePlayer = gamePlayer

                override fun updateItem(): ItemStack {
                    return ItemBuilder(Material.SKULL_ITEM)
                        .skull(this.gamePlayer.name)
                        .displayName("§e§l${this.gamePlayer.displayName}")
                        .lore("§f클릭 시 §e순간이동§f합니다.")
                        .build()
                }

                override fun onClick(event: InventoryGuiClickEvent) {
                    val player = event.player

                    if (!this.gamePlayer.isPlaying()) {
                        player.sendWarningSafely("이미 탈락한 플레이어입니다.")
                        return
                    }

                    if (!this.gamePlayer.isOnline) {
                        player.sendWarningSafely("접속 중이 아닌 플레이어입니다.")
                        return
                    }

                    TeleportUtil.teleport(player, this.gamePlayer.player.location)
                    player.sendMessage("${this.gamePlayer.displayName} §e님께 텔레포트했습니다.")
                }
            })
        }
    }

}