package net.pooleaf.gameprofile.guis

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.pageable.LargePageableGui
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.core.modules.support.common.util.toMillis
import net.pooleaf.gamehistory.GameHistoryApi
import org.bukkit.inventory.ItemStack
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

class GameHistoryGui(val commonPlayer: CommonPlayer<*>) : LargePageableGui("${commonPlayer.displayName} §0님의 게임 기록") {

    companion object {
        const val COUNT_PER_PAGE = 36
    }


    override fun getPageItems(page: Int): List<Any> {
        val gameInfoDtos = GameHistoryApi.unsafe.sqlManager.gameDao.selectRecentPlayerGameInfoByPlayerUuid(commonPlayer.uuid.toString(), COUNT_PER_PAGE, (page - 1) * COUNT_PER_PAGE)

        val icons = gameInfoDtos.map {
            object : InventoryIcon() {
                val gameInfoDto = it

                override fun updateItem(): ItemStack {
                    val channel = ChannelModule.getChannel(gameInfoDto.channelName)
                    val channelName = channel.group?.displayName ?: channel.displayName

                    val startedAt = gameInfoDto.startedAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss"))
                    val endedAt = gameInfoDto.endedAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss"))

                    val winOrLose = if (gameInfoDto.winCount > 0) "§a승리" else "§c패배"

                    val isCancelled = gameInfoDto.cancelYn == "Y"

                    val itemCode = when {
                        isCancelled -> "351:8"
                        gameInfoDto.winCount > 0 -> "351:10"
                        else -> "351:13"
                    }

                    val builder = ItemBuilder(itemCode)
                        .displayName("§f§l${channelName}")

                    if (isCancelled) {
                        builder.lore("§7취소된 게임")
                    } else {
                        builder.lore("${winOrLose}")
                    }

                    builder.lore("§f")

                    if (startedAt != null) {
                        builder.lore("§e게임 시작: §f${startedAt}")
                    }

                    if (endedAt != null) {
                        builder.lore("§e게임 종료: §f${endedAt}")

                        if (startedAt != null) {
                            val runningMillis = gameInfoDto.endedAt!!.toMillis() - gameInfoDto.startedAt!!.toMillis()
                            val runningTime = StringUtil.buildTimeStringFromMillis(runningMillis)
                            builder.lore("§e진행 시간: §f${runningTime}")
                        }
                    }

                    builder.lore("§f")
                        .lore("§e킬: §f${gameInfoDto.killCount}")
                        .lore("§e데스: §f${gameInfoDto.deathCount}")

                    if (gameInfoDto.replayId != null) {
                        builder.lore("§f")
                            .lore("§f클릭 시 리플레이가 재생됩니다.")
                    }

                    return builder.build()
                }

                override fun onClick(event: InventoryGuiClickEvent) {
                    if (gameInfoDto.replayId == null) return

                    event.player.performCommand("리플레이 재생 ${gameInfoDto.replayId}")
                }
            }
        }

        return icons
    }

    override fun getMaxPage(): Int {
        val count = GameHistoryApi.unsafe.sqlManager.gameDao.selectGameCountByPlayerUuid(commonPlayer.uuid.toString())
        return ceil((count.toFloat()) / COUNT_PER_PAGE).toInt()
    }

}