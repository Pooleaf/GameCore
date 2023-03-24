package net.pooleaf.gamereplay.commands

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.core.modules.support.common.pageable.PageableCommand
import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPermission
import net.pooleaf.gamereplay.sql.dtos.ReplayDto
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.ceil

class ReplayCommand {

    @Command(
        name = ["리플레이", "replay"],
        helpCommand = true,
        description = "리플레이 명령어를 확인합니다.",
    )
    fun replay(sender: CommonCommandSender<CommandSender>, result: HelpCommandResult) {
    }

    @Command(
        parent = ["리플레이"],
        name = ["재생", "play"],
        arguments = "<게임ID> (초)",
        description = "해당 리플레이를 재생합니다."
    )
    fun replay_play(player: CommonPlayer<Player>, result: CommandResult) {
        // 게임 ID 입력
        var gameId: UUID
        try {
            gameId = UUID.fromString(result.getArgument(0))
        } catch (exception: Exception) {
            player.sendWarning("잘못된 게임 ID 입니다.")
            return
        }

        // 리플레이 찾기
        val replay = GameReplayApi.unsafe.replayService.selectReplayDtoNoCache(gameId, false)
        if (replay == null) {
            player.sendWarning("리플레이를 찾을 수 없습니다.")
            return
        }

        // 시점
        val seconds = result.getArgumentAsLong(1) ?: 0
        val tick = (seconds.toFloat() / 20).toLong()

        // 리플레이 재생 채널이 아닐경우 리플레이 서버로 전송
        if (!GameReplayApi.replayConfig.isReplayPlayServer) {
            val sentChannel = GameReplayApi.unsafe.channelManager.sendToReplayChannel(player.platformSender, replay.gameId, tick)
            if (sentChannel == null) {
                player.sendWarning("리플레이 채널로 이동할 수 없습니다.")
                return
            }
            return
        }

        BukkitSyncScope.launch {
            // 다른 리플레이를 보고있을 경우 종료
            if (GameReplayApi.unsafe.replayService.isPlayingReplay(player.platformSender)) {
                GameReplayApi.unsafe.replayService.exitReplay(player.platformSender, false)
            }

            // 재생
            GameReplayApi.unsafe.replayService.playReplay(player.platformSender, gameId, tick)
        }
    }

    @Command(
        parent = ["리플레이"],
        name = ["일시정지", "pause"],
        description = "리플레이를 일시정지합니다."
    )
    fun replay_pause(player: CommonPlayer<Player>, result: CommandResult) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uuid)
        if (replayPlayer == null) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        if (!replayPlayer.isRunning()) {
            player.sendWarning("이미 일시정지 중입니다.")
            return
        }

        replayPlayer.pause()
        player.sendMessage("§e리플레이를 일시정지합니다.")
    }

    @Command(
        parent = ["리플레이"],
        name = ["일시정지해제", "unpause"],
        description = "리플레이 일시정지를 해제합니다."
    )
    fun replay_unpause(player: CommonPlayer<Player>, result: CommandResult) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uuid)
        if (replayPlayer == null) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        if (replayPlayer.isRunning()) {
            player.sendWarning("이미 재생 중입니다.")
            return
        }

        if (replayPlayer.currentTick >= replayPlayer.replay.endTick) {
            player.sendWarning("리플레이의 마지막 지점에서는 재생할 수 없습니다.")
            return
        }

        replayPlayer.play()
        player.sendMessage("§e리플레이를 재생합니다.")
    }

    @Command(
        parent = ["리플레이"],
        name = ["종료", "exit"],
        description = "리플레이를 종료합니다."
    )
    fun replay_exit(player: CommonPlayer<Player>, result: CommandResult) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uuid)
        if (replayPlayer == null) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        BukkitSyncScope.launch {
            GameReplayApi.unsafe.replayService.exitReplay(player.platformSender)
        }
    }

    @Command(
        parent = ["리플레이"],
        name = ["이동", "goTo", "jumpTo"],
        arguments = "<초>",
        description = "해당 시점으로 이동합니다."
    )
    fun replay_goTo(player: CommonPlayer<Player>, result: CommandResult) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uuid)
        if (replayPlayer == null) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        val seconds = result.getArgumentAsLong(0) ?: 0
        val tick = seconds * 20L

        if (tick < 0) {
            player.sendWarning("0초보다 작을 수 없습니다.")
            return
        }

        if (tick > replayPlayer.replay.endTick) {
            val endSeconds = (replayPlayer.replay.endTick.toFloat() / 20).toInt()
            player.sendWarning("${endSeconds}§e초 보다 클 수 없습니다.")
            return
        }

        replayPlayer.jumpTo(tick)

        val timeString = StringUtil.buildTimeStringFromSeconds(seconds, CommonChatColor.WHITE, CommonChatColor.YELLOW)
        player.sendMessage("${timeString}§e로 이동합니다.")
    }

    @Command(
        parent = ["리플레이"],
        name = ["목록", "list"],
        arguments = "(페이지)",
        description = "리플레이 목록을 확인합니다.",
        color = CommonChatColor.AQUA,
        permission = GameReplayPermission.ADMIN
    )
    fun replay_list(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val count = GameReplayApi.unsafe.replayService.selectReplayCount()
        object : PageableCommand<ReplayDto>(result.entered, listOf(), 7) {
            init {
                valueCount = count.toInt()
                maxPage = ceil(count.toFloat() / countPerPage).toInt()
            }

            override fun getHeaderColor(): CommonChatColor {
                return CommonChatColor.RED
            }

            override fun getHeaderMessage(): String {
                return "리플레이 목록"
            }

            override fun getPage(page: Int): List<ReplayDto> {
                return GameReplayApi.unsafe.replayService.selectReplayDtoListNoCache(null, countPerPage, (page - 1) * 7)
            }

            override fun handleValue(replayDto: ReplayDto, i: Int): Any {
                val createdTime = replayDto.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss"))

                val seconds = (replayDto.endTick.toFloat() / 20).toLong()
                val runningTime = StringUtil.buildTimeStringFromSeconds(seconds, CommonChatColor.WHITE, CommonChatColor.RED)

                return SimpleComponentBuilder("§c[ $i ] §f${replayDto.gameId} §c| §f${createdTime} §c| §f${runningTime}")
                    .hoverShowText("§e클릭 시 §f${replayDto.gameId} §e리플레이를 재생합니다.")
                    .clickRunCommand("/리플레이 재생 ${replayDto.gameId}")
                    .build()
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

    @Command(
        parent = ["리플레이"],
        name = ["스폰설정", "setSpawn"],
        description = "현재 위치 스폰 위치로 설정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameReplayPermission.ADMIN
    )
    fun replay_setSpawn(player: CommonPlayer<Player>, result: CommandResult) {
        GameReplayApi.spawnConfig.spawnLocation = player.platformSender.location
        GameReplayApi.spawnConfig.save()

        player.sendMessage("§b현재 위치를 스폰 위치로 설정했습니다.")
    }
    
}