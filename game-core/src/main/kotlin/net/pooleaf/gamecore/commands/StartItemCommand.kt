package net.pooleaf.gamecore.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.core.modules.support.common.pageable.PageableCommand
import net.pooleaf.gamecore.GameCore
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
        arguments = "<기본|등급이름>",
        description = "기본 시작 아이템 또는 등급 시작 아이템을 수정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun startItem_edit(player: CommonPlayer<Player>, result: CommandResult) {
        val rankName = result.enteredArguments
        StartItemEditGui(rankName).open(player.platformSender)
    }

    @Command(
        parent = ["시작아이템", "게임 시작아이템"],
        name = ["목록", "list"],
        arguments = "(페이지)",
        description = "시작 아이템 목록을 확인합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun startItem_list(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val startItemRankNames = GameCore.unsafe.startItemManager.datas.keys.toList().sorted()

        object : PageableCommand<String>(result.entered, startItemRankNames, 7) {
            override fun getHeaderColor(): CommonChatColor {
                return CommonChatColor.AQUA
            }

            override fun getHeaderMessage(): String {
                return "시작 아이템 목록"
            }

            override fun handleValue(rankName: String, i: Int): Any {
                return SimpleComponentBuilder("$headerColor[ ${i + 1} ] §f${rankName}")
                    .hoverShowText("§b클릭 시 §f${rankName} §b시작 아이템을 수정합니다.")
                    .clickRunCommand("/게임 시작아이템 수정 ${rankName}")
                    .build()
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

    @Command(
        parent = ["시작아이템", "게임 시작아이템"],
        name = ["삭제", "delete"],
        arguments = "<등급이름>",
        description = "시작 아이템을 삭제합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun startItem_delete(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val rankName = result.enteredArguments
        GameCore.unsafe.startItemService.deleteStartItemConfig(rankName)
    }

}