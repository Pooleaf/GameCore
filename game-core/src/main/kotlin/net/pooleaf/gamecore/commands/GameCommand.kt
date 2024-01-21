package net.pooleaf.gamecore.commands

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameCommand {

    @Command(
        name = ["게임", "game"],
        description = "게임 명령어를 확인합니다.",
        helpCommand = true,
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game(sender: CommandSender, result: HelpCommandResult) {
    }

    @Command(
        parent = ["게임"],
        name = ["시작", "start"],
        description = "게임을 시작시킵니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_start(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        BukkitSyncScope.launch {
            if (GameCore.game.isRunning) {
                sender.sendWarning("이미 게임이 시작되었습니다.")
                return@launch
            }

            GameCore.game.start(sender.platformSender)

            BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 게임을 시작시켰습니다.")
        }
    }

    @Command(
        parent = ["게임"],
        name = ["중단", "중지", "stop"],
        description = "게임을 중단시킵니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_stop(sender: CommonCommandSender<CommandSender>, result: CommandResult?) {
        if (!GameCore.game.isRunning) {
            sender.sendWarning("아직 게임 중이 아닙니다.")
            return
        }

        BukkitAsyncScope.launch {
            GameCore.game.cancel(sender.platformSender, "${sender.displayName} 님께서 게임을 중단시켰습니다.")
            BukkitBroadcaster.broadcastWarning("${sender.displayName} §c님께서 게임을 중단시켰습니다.")
        }
    }

    @Command(
        parent = ["", "게임"],
        name = ["관전", "rhkswjs", "spectator"],
        description = "관전 모드로 전환하거나 해제합니다.",
        async = false
    )
    fun game_spectator(player: CommonPlayer<Player>, result: CommandResult?) {
        BukkitSyncScope.launch {
            // 대기 중에만 사용 가능. 단 관리자는 아무 때나 사용할 수 있음
            if (GameCore.game.isRunning && !player.platformSender.isOp) {
                player.sendWarning("게임 중에는 사용할 수 없습니다.")
                return@launch
            }

            val gamePlayer = GameCore.unsafe.playerManager.get(player.uuid)

            // 관전 모드로 전환
            if (!gamePlayer.isSpectator) {
                GameCore.unsafe.playerService.enableSpectatorMode(gamePlayer)
                BukkitBroadcaster.broadcast("${gamePlayer.displayName} §b님께서 관전을 시작했습니다.")
            }
            // 관전 모드 해제
            else {
                // 관리자는 게임 중에도 관전을 해제할 수 있으나, 게임 종료 후에는 불가능
                if (GameCore.game.isEnded) {
                    player.sendWarning("게임이 종료되어 관전을 해제할 수 없습니다.")
                    return@launch
                }

                GameCore.unsafe.playerService.disableSpectatorMode(gamePlayer)
                BukkitBroadcaster.broadcast("${gamePlayer.displayName} §b님께서 관전을 종료했습니다.")
            }
        }
    }

    @Command(
        parent = ["", "게임"],
        name = ["플레이어목록", "playerList", "list"],
        description = "게임에 참여 중인 플레이어 목록을 확인합니다."
    )
    fun game_playerList(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        // 참여자
        val playerNames = if (!GameCore.game.isGameStarted) {
            GameCore.unsafe.playerManager.getOnlinePlayingPlayers().joinToString { it.displayName }
        } else {
            GameCore.unsafe.teamManager.teams.joinToString { team ->
                // 플레이어 이름 가공
                val teamPlayerNames = team.players.joinToString { gamePlayer ->
                    // 오프라인 회색
                    if (!gamePlayer.isOnline) {
                        "§7${gamePlayer.displayName}"
                    }
                    // 탈락
                    else if (gamePlayer.isDefeated) {
                        "§7${gamePlayer.displayName}(탈락)"
                    } else {
                        "§f${gamePlayer.displayName}"
                    }
                }

                // 팀 가공
                if (GameCore.teamConfig.playerCountPerTeam == 1) {
                    teamPlayerNames
                } else {
                    // 탈락한 팀은 회색
                    if (team.isDefeated()) {
                        "§7($teamPlayerNames)"
                    } else {
                        "§f($teamPlayerNames)"
                    }
                }
            }
        }

        val playerCount = GameCore.unsafe.playerManager.getOnlinePlayingPlayers().size
        sender.sendMessage("§c참여자($playerCount): §f$playerNames")

        // 관전자
        if (GameCore.unsafe.playerManager.getOnlineSpectators().isNotEmpty()) {
            val spectatorPlayerCount = GameCore.unsafe.playerManager.getOnlineSpectators().size
            val spectatorPlayerNames = GameCore.unsafe.playerManager.getOnlineSpectators().joinToString { it.displayName }
            sender.sendMessage("§b관전자($spectatorPlayerCount): §f${spectatorPlayerNames}")
        }

    }

    @Command(
        parent = ["", "게임"],
        name = ["재부팅예약", "scheduleReboot"],
        description = "서버 재부팅을 예약합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_scheduleReboot(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        GameCore.unsafe.rebootScheduled = true
        sender.sendMessage("§a서버 재부팅을 예약했습니다.")

        // 게임 중이 아니면 즉시 재부팅
        if (!GameCore.game.isCountingStarted) {
            ChannelModule.getCurrentChannel().isAllowFastJoin = false
            ChannelModule.getCurrentChannel().save()

            BukkitAsyncScope.launch {
                BukkitBroadcaster.broadcast("§c서버 재부팅을 위해 로비로 이동됩니다.")
                BukkitBroadcaster.broadcastTitle("§c서버 재부팅을 위해 로비로 이동됩니다.")

                delay(1000L)
                Bukkit.getOnlinePlayers().forEach { ChannelModule.getLobbyChannelGroup().fastJoin(it.uniqueId) }

                delay(5000L)
                Bukkit.shutdown()
            }
        }
    }

    @Command(
        parent = ["", "게임"],
        name = ["재부팅예약취소", "cancelScheduleReboot"],
        description = "서버 재부팅 예약을 취소합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_cancelScheduleReboot(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        GameCore.unsafe.rebootScheduled = false
        sender.sendMessage("§c서버 재부팅 예약을 취소했습니다.")
    }

}