package net.pooleaf.gamereplay.quickbar

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.pageable.LargePageableGui
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ReplayTeleporterGui(
    val replayPlayer: ReplayPlayer
): LargePageableGui("관전할 플레이어를 선택하세요.") {

    override fun onUpdate() {
        clear()

        replayPlayer.virtualPlayerManager.values()
            .filter { !it.isDefeated && it.citizensNpc.entity != null }
            .forEach { virtualPlayer ->
            val commonPlayer = CommonSenderModule.getPlayer(virtualPlayer.uuid)

            addItem(object : InventoryIcon() {
                val uuid = virtualPlayer.uuid
                val targetCommonPlayer = commonPlayer

                override fun updateItem(): ItemStack {
                    return if (commonPlayer == null) {
                        ItemBuilder(Material.SKULL_ITEM)
                            .displayName("§e§l${this.uuid}")
                            .lore("§f클릭 시 §e순간이동§f합니다.")
                            .build()
                    } else {
                        ItemBuilder(Material.SKULL_ITEM)
                            .skull(this.targetCommonPlayer.name)
                            .displayName("§e§l${this.targetCommonPlayer.displayName}")
                            .lore("§f클릭 시 §e순간이동§f합니다.")
                            .build()
                    }
                }

                override fun onClick(event: InventoryGuiClickEvent) {
                    val player = event.player

                    val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uniqueId) ?: return
                    val virtualPlayer = replayPlayer.virtualPlayerManager.get(this.uuid) ?: return

                    TeleportUtil.teleport(player, virtualPlayer.location)
                    player.sendMessage("${this.targetCommonPlayer.displayName} §e님께 텔레포트했습니다.")
                }
            })
        }
    }

}