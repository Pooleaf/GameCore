package net.pooleaf.gamereplay.quickbar

import com.cryptomorin.xseries.XSound
import me.arcaniax.hdb.api.HeadDatabaseAPI
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.gui.bukkit.quickbar.QuickBar
import net.pooleaf.core.modules.gui.bukkit.quickbar.Slot
import net.pooleaf.core.modules.gui.bukkit.quickbar.event.SlotClickEvent
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.replay.ReplayPlayer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ReplayQuickBar(
    val replayPlayer: ReplayPlayer
): QuickBar() {

    init {
        // 클릭 속도
        clickDelayMillis = 1000L


        // 텔레포터 슬롯
        val teleporterSlot = object : Slot() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.COMPASS)
                    .displayName("§e§l순간이동기 §f§l(우클릭)")
                    .lore("§f우클릭 시 순간이동할 플레이어를 선택할 수 있습니다.")
                    .build()
            }

            override fun onClick(event: SlotClickEvent) {
                val player = event.player
                ReplayTeleporterGui(player).open(player)
            }
        }

        // 재생 속도 느리게 슬롯
        // https://minecraft-heads.com/custom-heads/miscellaneous/32816-chapter-back-active
        // TODO
        val slowSlot = object : Slot() {
            override fun updateItem(): ItemStack {
                // https://minecraft-heads.com/custom-heads/alphabet/17841-backward
                return ItemBuilder(HeadDatabaseAPI().getItemHead("17841"))
                    .displayName("§e§l느리게 §f§l(우클릭)")
                    .lore("§f우클릭 시 재생 속도를 늦춥니다.")
                    .build()
            }

            override fun onClick(event: SlotClickEvent) {
                val player = event.player

                val isRunning = replayPlayer.isRunning()
                if (isRunning) {
                    replayPlayer.pause()
                }

                var playSpeed = replayPlayer.playSpeed - 0.5F
                if (playSpeed <= 0.5F) {
                    playSpeed = 0.5F
                }

                replayPlayer.playSpeed = playSpeed

                if (isRunning) {
                    replayPlayer.play()
                }

                XSound.UI_BUTTON_CLICK.play(player, 0.3F, 0.7F)
            }
        }

        // 뒤로가기 슬롯
        val goBackSlot = object : Slot() {
            override fun updateItem(): ItemStack {
                val replaySkipSeconds = GameReplayApi.replayConfig.replaySkipSeconds

                // https://minecraft-heads.com/custom-heads/alphabet/17841-backward
                return ItemBuilder(HeadDatabaseAPI().getItemHead("17841"))
                    .displayName("§f§l${replaySkipSeconds}§e§l초 뒤로가기 §f§l(우클릭)")
                    .lore("§f우클릭 시 10초 전으로 돌아갑니다.")
                    .build()
            }

            override fun onClick(event: SlotClickEvent) {
                val player = event.player

                val isRunning = replayPlayer.isRunning()
                if (isRunning) {
                    replayPlayer.pause()
                }

                val replaySkipSeconds = GameReplayApi.replayConfig.replaySkipSeconds
                var newTick = replayPlayer.currentTick - (replaySkipSeconds * 20 * 10)
                if (newTick < 0) {
                    newTick = 0.0F
                }

                replayPlayer.jumpTo(newTick.toLong())

                if (isRunning) {
                    replayPlayer.play()
                }

                XSound.UI_BUTTON_CLICK.play(player, 0.3F, 0.7F)
            }
        }

        // 일시정지/재생 슬롯
        val pausePlaySlot = object : Slot() {
            override fun updateItem(): ItemStack {
                return if (!replayPlayer.isRunning()) {
                    ItemBuilder("351:8")
                        .displayName("§a§l재생 §f§l(우클릭)")
                        .lore("§f우클릭 시 리플레이를 재생합니다.")
                        .build()
                } else {
                    ItemBuilder("351:10")
                        .displayName("§7§l일시정지 §f§l(우클릭)")
                        .lore("§f우클릭 시 리플레이를 일시정지합니다.")
                        .build()
                }
            }

            override fun onClick(event: SlotClickEvent) {
                val player = event.player

                // 재생
                if (!replayPlayer.isRunning()) {
                    replayPlayer.play()
                    XSound.UI_BUTTON_CLICK.play(player, 0.3F, 0.7F)
                }
                // 일시정지
                else {
                    replayPlayer.pause()
                    XSound.UI_BUTTON_CLICK.play(player, 0.3F, 0.7F)
                }

                updateAsynchronously()
                player.updateInventory()
            }
        }

        // 건너뛰기 슬롯
        val skipSlot = object : Slot() {
            override fun updateItem(): ItemStack {
                val replaySkipSeconds = GameReplayApi.replayConfig.replaySkipSeconds

                // https://minecraft-heads.com/custom-heads/alphabet/2301-forward
                return ItemBuilder(HeadDatabaseAPI().getItemHead("2301"))
                    .displayName("§f§l${replaySkipSeconds}§e§l초 건너뛰기 §f§l(우클릭)")
                    .lore("§f우클릭 시 10초 후로 건너뜁니다.")
                    .build()
            }

            override fun onClick(event: SlotClickEvent) {
                val player = event.player

                val isRunning = replayPlayer.isRunning()
                if (isRunning) {
                    replayPlayer.pause()
                }

                val replaySkipSeconds = GameReplayApi.replayConfig.replaySkipSeconds
                var newTick = replayPlayer.currentTick + (replaySkipSeconds * 20 * 10)
                if (newTick > replayPlayer.replay.endTick) {
                    newTick = replayPlayer.replay.endTick.toFloat()
                }

                replayPlayer.jumpTo(newTick.toLong())

                if (isRunning) {
                    replayPlayer.play()
                }

                XSound.UI_BUTTON_CLICK.play(player, 0.3F, 0.7F)
            }
        }

        // 재생 속도 빠르게 슬롯
        // https://minecraft-heads.com/custom-heads/miscellaneous/32818-next-chapter-active
        // TODO

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

        setSlot(4, goBackSlot)
        setSlot(5, pausePlaySlot)
        setSlot(6, skipSlot)

        setSlot(9, lobbySlot)


        updateAsynchronously()
    }

}