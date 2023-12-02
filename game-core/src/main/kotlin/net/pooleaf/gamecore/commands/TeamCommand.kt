package net.pooleaf.gamecore.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.pageable.PageableCommand
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import net.pooleaf.gamecore.team.Team
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TeamCommand {

    @Command(
        parent = ["", "게임"],
        name = ["팀", "team"],
        description = "내 팀 목록을 확인합니다."
    )
    fun team(player: Player, result: CommandResult) {
        val gamePlayer = player.toGamePlayer()!!

        val team = gamePlayer.team
        if (team == null) {
            player.sendWarning("팀이 없습니다.")
            return
        }

        player.sendMessage("§2[ 내 팀 ]")
        team.players.forEach {
            if (!gamePlayer.isOnline) {
                player.sendMessage("§7${it.displayName}: 오프라인")
            } else if (gamePlayer.isDefeated) {
                player.sendMessage("§c${it.displayName}: 탈락")
            } else {
                player.sendMessage("§f${it.displayName}: 게임 중")
            }
        }
    }

    @Command(
        parent = ["팀"],
        name = ["도움말", "help", "?"],
        description = "팀 명령어 도움말을 확인합니다.",
        helpCommand = true,
        helpCommandTarget = "팀"
    )
    fun help(sender: CommonCommandSender<CommandSender>, result: HelpCommandResult) {
    }

    @Command(
        parent = ["팀"],
        name = ["목록", "list"],
        arguments = "(페이지)",
        description = "팀 목록을 확인합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun list_all(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        object : PageableCommand<Team>(result.entered, GameCore.unsafe.teamManager.teams, 5) {
            override fun getHeaderColor(): CommonChatColor {
                return CommonChatColor.DARK_GREEN
            }

            override fun getHeaderMessage(): String {
                return "팀 목록"
            }

            override fun handleValue(team: Team, i: Int): Any {
                val playersText = team.players.map {
                    if (!it.isOnline) {
                        "§7${it.displayName}(오프라인)"
                    } else if (it.isDefeated) {
                        "§c${it.displayName}(탈락)"
                    } else {
                        "§f${it.displayName}(게임 중)"
                    }
                }.joinToString()

                return "§a[${i + 1}번 팀] ${playersText}"
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

}