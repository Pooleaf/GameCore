package net.pooleaf.gamereplay.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class ReplayTestCommand {

    @Command(
        name = ["rtest"],
        helpCommand = true,
        permission = GameReplayPermission.ADMIN
    )
    fun replay_test(sender: CommandSender, result: HelpCommandResult) {
    }

    @Command(
        parent = ["rtest"],
        name = ["startRecord"],
        description = "start record",
        permission = GameReplayPermission.ADMIN
    )
    fun replay_test_startRecord(player: Player, result: CommandResult) {
        if (GameReplayApi.unsafe.recordManager.isRecording()) {
            player.sendWarning("이미 녹화 중입니다.")
            return
        }

        val uuid = UUID.randomUUID()
        val targetPlayerUuids = GameCore.unsafe.playerManager.getJoinedPlayers().map { it.uuid }
        GameReplayApi.unsafe.recordManager.startRecord(
            uuid,
            targetPlayerUuids,
            player.world.name,
            player.location.x,
            player.location.y,
            player.location.z
        )

        player.sendMessage("${uuid} 녹화를 시작했습니다.")
    }

    @Command(
        parent = ["rtest"],
        name = ["stopRecord"],
        description = "stop record",
        permission = GameReplayPermission.ADMIN
    )
    fun replay_test_stopRecord(sender: CommandSender, result: CommandResult) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) {
            sender.sendWarning("녹화 중이 아닙니다.")
            return
        }

        val record = GameReplayApi.unsafe.recordManager.record!!
        GameReplayApi.unsafe.recordManager.endRecord()

        sender.sendMessage("${record.replay.gameId} 녹화를 중지했습니다.")
    }

    @Command(
        parent = ["rtest"],
        name = ["startReplay"],
        arguments = "(gameId)",
        description = "replay",
        permission = GameReplayPermission.ADMIN,
        async = false
    )
    fun replay_test_startReplay(player: Player, result: CommandResult) {
        if (result.argumentsLength > 0) {
            val gameId = result.getArgument(0)
            val replay = GameReplayApi.unsafe.replayService.loadReplayFromDatabase(UUID.fromString(gameId))

            if (replay == null) {
                player.sendWarning("리플레이가 존재하지 않습니다.")
                return
            }

            GameReplayApi.unsafe.replayService.playReplay(player, replay.gameId)

            player.sendMessage("${replay.gameId} 리플레이를 시작합니다.")
        } else {
            if (GameReplayApi.unsafe.recordManager.record == null) {
                player.sendWarning("녹화가 없습니다.")
                return
            }

            val record = GameReplayApi.unsafe.recordManager.record!!
            val replayUuid = record.replay.gameId

            GameReplayApi.unsafe.replayService.playReplay(player, replayUuid)

            player.sendMessage("${replayUuid} 리플레이를 시작합니다.")
        }
    }

    @Command(
        parent = ["rtest"],
        name = ["exitReplay"],
        description = "replay",
        permission = GameReplayPermission.ADMIN,
        async = false
    )
    fun replay_test_exitReplay(player: Player, result: CommandResult) {
        if (!GameReplayApi.unsafe.replayService.isPlayingReplay(player)) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uniqueId)
        GameReplayApi.unsafe.replayService.exitReplay(player)

        player.sendMessage("${replayPlayer.replay.gameId} 리플레이를 종료합니다.")
    }

    @Command(
        parent = ["rtest"],
        name = ["jumpTo"],
        arguments = "<Tick>",
        description = "replay jump to",
        permission = GameReplayPermission.ADMIN,
    )
    fun replay_test_jumpTo(player: Player, result: CommandResult) {
        if (!GameReplayApi.unsafe.replayService.isPlayingReplay(player)) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        val tick = result.getArgumentAsLong(0)
        if (tick == null) {
            player.sendWarning("Tick은 정수만 입력할 수 있습니다.")
            return
        }

        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uniqueId)
        if (tick > replayPlayer.replay.endTick) {
            player.sendWarning("Tick이 리플레이 길이보다 큽니다. (리플레이 길이: ${replayPlayer.replay.endTick})")
            return
        }

        if (replayPlayer.isRunning()) {
            replayPlayer.pause()
        }

        replayPlayer.jumpTo(tick)
        replayPlayer.play()

        player.sendMessage("${tick} Tick을 재생합니다.")
    }

}