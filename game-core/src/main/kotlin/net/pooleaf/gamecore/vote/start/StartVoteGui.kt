package net.pooleaf.gamecore.vote.start

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryGui
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class StartVoteGui(): InventoryGui("시작 투표", 3) {

    init {
        // 찬성 아이콘
        val agreeIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                val agreePlayerCount = GameCore.unsafe.startVoteManager.startVote.agreePlayers.size

                return ItemBuilder("159:5")
                    .amount(agreePlayerCount)
                    .displayName("§a§l찬성하기 §a(§f${agreePlayerCount}§a명)")
                    .lore("§f클릭 시 시작 투표에 §a찬성§f합니다.")
                    .build()
            }

            override fun onClick(event: InventoryGuiClickEvent) {
                val gamePlayer = GameCore.unsafe.playerManager.get(event.player.uniqueId)

                gamePlayer?.let {
                    GameCore.unsafe.startVoteManager.voteToAgree(it)
                    it.playSoundSafely(XSound.UI_BUTTON_CLICK)

                    event.player.closeInventory()
                }
            }
        }

        // 반대 아이콘
        val disagreeIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                val disagreePlayerCount = GameCore.unsafe.startVoteManager.startVote.disagreePlayers.size

                return ItemBuilder("159:14")
                    .amount(disagreePlayerCount)
                    .displayName("§c§l반대하기 §c(§f${disagreePlayerCount}§c명)")
                    .lore("§f클릭 시 시작 투표에 §c찬성§f합니다.")
                    .build()
            }

            override fun onClick(event: InventoryGuiClickEvent) {
                val gamePlayer = GameCore.unsafe.playerManager.get(event.player.uniqueId)

                gamePlayer?.let {
                    GameCore.unsafe.startVoteManager.voteToDisagree(it)
                    it.playSoundSafely(XSound.UI_BUTTON_CLICK)

                    event.player.closeInventory()
                }
            }
        }

        // 장식 아이템 배치
        val deco = ItemBuilder(Material.STAINED_GLASS_PANE).displayName("§f").build()
        for (i in 0 until 27) {
            mainPanel.set(i, deco)
        }

        // 아이콘 배치
        mainPanel.set(3, 2, agreeIcon)
        mainPanel.set(7, 2, disagreeIcon)

        updateAsynchronously()
    }

}