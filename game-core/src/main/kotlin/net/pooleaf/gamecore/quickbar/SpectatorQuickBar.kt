package net.pooleaf.gamecore.quickbar

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.gui.bukkit.quickbar.QuickBar
import net.pooleaf.core.modules.gui.bukkit.quickbar.Slot
import net.pooleaf.core.modules.gui.bukkit.quickbar.event.SlotClickEvent
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SpectatorQuickBar: QuickBar() {

    val spectatorTeleporterGui = SpectatorTeleporterGui()


    init {
        val teleporterSlot = object : Slot() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.COMPASS)
                    .displayName("§e§l순간이동기 §f§l(우클릭)")
                    .lore("§f우클릭 시 순간이동할 플레이어를 선택할 수 있습니다.")
                    .build()
            }

            override fun onClick(event: SlotClickEvent) {
                spectatorTeleporterGui.open(event.player)
            }
        }

        // 로비로 이동하기 슬롯
        val lobbySlot = object : Slot() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.BED)
                    .displayName("§e§l로비로 이동 §f§l(우클릭)")
                    .lore("§f우클릭 시 로비로 이동합니다.")
                    .build()
            }

            override fun onClick(event: SlotClickEvent) {
                val player = event.player

                player.sendMessage("§e로비로 이동합니다.")

                ChannelModule.getLobbyChannelGroup().fastJoin(player.uniqueId)
            }
        }

        // 슬롯 배치
        setSlot(1, teleporterSlot)
        setSlot(9, lobbySlot)

        updateAsynchronously()
    }

}