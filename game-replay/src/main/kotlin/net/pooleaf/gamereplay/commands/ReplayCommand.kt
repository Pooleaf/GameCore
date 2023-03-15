package net.pooleaf.gamereplay.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

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

        // 다른 리플레이를 보고있을 경우 종료
        if (GameReplayApi.unsafe.replayService.isPlayingReplay(player.platformSender)) {
            GameReplayApi.unsafe.replayService.exitReplay(player.platformSender)
        }

        // 재생
        val replayPlayer = GameReplayApi.unsafe.replayService.playReplay(player.platformSender, gameId)
        if (tick > 0) {
            replayPlayer.jumpTo(tick)
        }
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

        replayPlayer.exit()
        GameReplayApi.unsafe.replayPlayerManager.remove(player.uuid)
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

        val seconds = result.getArgumentAsLong(1) ?: 0
        val tick = (seconds.toFloat() / 20).toLong()

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
    
}