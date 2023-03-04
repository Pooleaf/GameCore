package net.pooleaf.gamecore.quickbar

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.gui.bukkit.quickbar.FakeSlot
import net.pooleaf.core.modules.gui.bukkit.quickbar.QuickBar
import net.pooleaf.core.modules.gui.bukkit.quickbar.Slot
import net.pooleaf.core.modules.gui.bukkit.quickbar.event.SlotClickEvent
import net.pooleaf.core.modules.support.bukkit.messager.sendWarningSafely
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class WaitingQuickBar: QuickBar() {

    init {
        // 시작 투표 슬롯
        var startVoteSlot: Slot? = null
        if (GameCore.quickBarConfig.useWaitingStartVote) {
            startVoteSlot = object : Slot() {
                override fun updateItem(): ItemStack {
                    return ItemBuilder(Material.PAPER)
                        .displayName("§e§l게임 시작 투표 §f§l(우클릭)")
                        .lore("§f우클릭 시 시작 투표에 참여할 수 있습니다.")
                        .build()
                }

                override fun onClick(event: SlotClickEvent) {
                    if (GameCore.game.phasePipeline.isRunning()) {
                        event.player.sendWarningSafely("게임이 시작되어 투표할 수 없습니다.")
                        return
                    }

                    val gamePlayer = GameCore.unsafe.playerManager.get(event.player.uniqueId)
                    if (gamePlayer.isSpectator) {
                        gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
                        return
                    }

                    GameCore.unsafe.startVoteManager.startVoteGui.open(event.player)
                }
            }
        }

        // 맵 투표 슬롯
        var mapVoteSlot: Slot? = null
        if (GameCore.quickBarConfig.useWaitingMapVote) {
            mapVoteSlot = object : Slot() {
                override fun updateItem(): ItemStack {
                    return ItemBuilder(Material.PAPER)
                        .displayName("§e§l맵 투표 §f§l(우클릭)")
                        .lore("§f우클릭 시 맵 투표에 참여할 수 있습니다.")
                        .build()
                }

                override fun onClick(event: SlotClickEvent) {
                    if (GameCore.game.isCountingStarted) {
                        event.player.sendWarningSafely("게임이 시작되어 투표할 수 없습니다.")
                        return
                    }

                    val gamePlayer = GameCore.unsafe.playerManager.get(event.player.uniqueId)
                    if (gamePlayer.isSpectator) {
                        gamePlayer.sendWarningSafely("관전 중에는 투표에 참여할 수 없습니다.")
                        return
                    }

                    GameCore.unsafe.mapVoteManager.mapVoteGui.open(event.player)
                }
            }
        }

        // 관전 모드 전환 슬롯
        val toggleSpectatorSlot = object : FakeSlot() {
            override fun updateItem(player: Player): ItemStack? {
                val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

                val itemStack = if (GameCore.game.isRunning) {
                    null
                } else {
                    if (gamePlayer?.let { it.isSpectator } == true) {
                        ItemBuilder(Material.IRON_SWORD)
                            .displayName("§b§l관전 모드 해제 §f§l(우클릭)")
                            .lore("§f우클릭 시 관전 모드를 해제합니다.")
                            .build()
                    } else {
                        ItemBuilder(Material.EYE_OF_ENDER)
                            .displayName("§b§l관전 모드로 전환 §f§l(우클릭)")
                            .lore("§f우클릭 시 관전 모드로 전환합니다.")
                            .build()
                    }
                }

                return itemStack
            }

            override fun onClick(event: SlotClickEvent) {
                val player = event.player
                val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

                BukkitSyncScope.launch {
                    if (gamePlayer.isSpectator) {
                        gamePlayer.disableSpectatorMode()
                        gamePlayer.sendMessageSafely("§b관전 모드를 해제했습니다.")
                        updateItem(player)
                    } else {
                        gamePlayer.enableSpectatorMode()
                        gamePlayer.sendMessageSafely("§b관전 모드로 전환되었습니다.")
                        updateItem(player)
                    }
                }
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
        setSlot(1, startVoteSlot)
        setSlot(if (startVoteSlot == null) 1 else 2, mapVoteSlot)
        setSlot(8, toggleSpectatorSlot)
        setSlot(9, lobbySlot)

        updateAsynchronously()
    }

}