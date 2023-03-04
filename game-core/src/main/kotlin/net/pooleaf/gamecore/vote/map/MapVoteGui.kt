package net.pooleaf.gamecore.vote.map

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.pageable.LargePageableGui
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MapVoteGui(): LargePageableGui ("맵 투표") {

    override fun onUpdate() {
        clear()

        // 랜덤 투표 아이콘
        val randomIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                val randomVotedCount = GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size - GameCore.unsafe.mapVoteManager.mapVote.getVoteCount()

                return ItemBuilder(Material.EMPTY_MAP)
                    .amount(randomVotedCount)
                    .displayName("§e§l랜덤 (§f${randomVotedCount}§e명)")
                    .lore("§e클릭 시 §f랜덤§e에 투표합니다.")
                    .build()
            }

            override fun onClick(event: InventoryGuiClickEvent) {
                val gamePlayer = GameCore.unsafe.playerManager.get(event.player.uniqueId)

                gamePlayer?.let {
                    GameCore.unsafe.mapVoteManager.voteToRandom(it)
                    it.playSoundSafely(XSound.UI_BUTTON_CLICK)

                    event.player.closeInventory()
                }
            }
        }

        addItem(randomIcon)

        GameCore.unsafe.mapManager.values().forEach { map ->
            val mapIcon = object : InventoryIcon() {
                val gameMap = map

                override fun updateItem(): ItemStack {
                    val votedCount = GameCore.unsafe.mapVoteManager.mapVote.getVoteCount(gameMap)

                    return ItemBuilder(Material.MAP)
                        .amount(votedCount)
                        .displayName("§e§l${gameMap.name} (§f${votedCount}§e명)")
                        .lore("§e클릭 시 §f${gameMap.name} §e맵에 투표합니다.")
                        .build()
                }

                override fun onClick(event: InventoryGuiClickEvent) {
                    val gamePlayer = GameCore.unsafe.playerManager.get(event.player.uniqueId)

                    gamePlayer?.let {
                        GameCore.unsafe.mapVoteManager.voteTo(gamePlayer, gameMap)
                        it.playSoundSafely(XSound.UI_BUTTON_CLICK)

                        event.player.closeInventory()
                    }
                }
            }

            addItem(mapIcon)
        }
    }

}