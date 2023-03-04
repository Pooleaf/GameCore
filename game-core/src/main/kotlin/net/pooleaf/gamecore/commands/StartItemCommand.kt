package net.pooleaf.gamecore.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.GameCorePermission
import net.pooleaf.gamecore.startitem.StartItemEditGui
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StartItemCommand {

    @Command(
        parent = ["", "게임"],
        name = ["시작아이템", "startItem"],
        description = "시작 아이템 명령어를 확인합니다.",
        helpCommand = true,
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun startItem(sender: CommonCommandSender<CommandSender>, result: HelpCommandResult) {
    }

    @Command(
        parent = ["시작아이템", "게임 시작아이템"],
        name = ["수정", "edit"],
        description = "시작 아이템을 수정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun startItem_edit(player: CommonPlayer<Player>, result: CommandResult) {
        StartItemEditGui().open(player.platformSender)
    }

}