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
import net.pooleaf.gamecore.kit.Kit
import net.pooleaf.gamecore.kit.KitEditGui
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KitCommand {

    @Command(
        parent = ["", "게임"],
        name = ["킷", "kit"],
        description = "킷 명령어를 확인합니다.",
        helpCommand = true,
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun kit(sender: CommonCommandSender<CommandSender>, result: HelpCommandResult) {
    }

    @Command(
        parent = ["킷", "게임 킷"],
        name = ["생성", "create"],
        arguments = "<킷이름>",
        description = "킷을 생성합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun kit_create(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (result.argumentsLength > 1) {
            sender.sendWarning("킷 이름은 띄어쓰기가 불가능합니다.")
            return
        }

        if (GameCore.unsafe.kitManager.exists(result.getArgument(0))) {
            sender.sendWarning("이미 존재하는 킷 이름입니다.")
            return
        }

        val kit = Kit()
        kit.name = result.getArgument(0)
        kit.saveKitConfig()
        GameCore.unsafe.kitManager.set(kit.name, kit)

        sender.sendMessage("${kit.name} §b킷을 생성했습니다.")
    }

    @Command(
        parent = ["킷", "게임 킷"],
        name = ["수정", "edit"],
        arguments = "<킷이름>",
        description = "킷의 아이템을 수정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun kit_edit(player: CommonPlayer<Player>, result: CommandResult) {
        val kitName = result.getArgument(0)

        if (!GameCore.unsafe.kitManager.exists(kitName)) {
            player.sendWarning("존재하지 않는 킷입니다.")
            return
        }

        val kit = GameCore.unsafe.kitManager.get(kitName)
        KitEditGui(kit, player.platformSender).open(player.platformSender)
    }

    @Command(
        parent = ["킷", "게임 킷"],
        name = ["삭제", "delete"],
        arguments = "<킷이름>",
        description = "킷을 삭제합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun kit_delete(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val kitName = result.getArgument(0)

        if (!GameCore.unsafe.kitManager.exists(kitName)) {
            sender.sendWarning("존재하지 않는 킷입니다.")
            return
        }

        GameCore.unsafe.kitManager.remove(kitName)
        GameCore.unsafe.kitService.deleteKitConfig(kitName)

        sender.sendMessage("${kitName} §b킷을 삭제했습니다.")
    }

    @Command(
        parent = ["킷", "게임 킷"],
        name = ["목록", "list"],
        arguments = "(페이지)",
        description = "킷 목록을 확인합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun kit_list(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val kits = GameCore.unsafe.kitManager.values().toList()
        if (kits.isEmpty()) {
            sender.sendWarning("생성된 킷이 없습니다.")
            return
        }

        object : PageableCommand<Kit>(result.entered, kits, 7) {
            override fun getHeaderColor(): CommonChatColor {
                return CommonChatColor.AQUA
            }

            override fun getHeaderMessage(): String {
                return "킷 목록"
            }

            override fun handleValue(kit: Kit, i: Int): Any {
                return SimpleComponentBuilder("$headerColor[ $i ] §f${kit.name}")
                    .hoverShowText("§b클릭 시 §f${kit.name} §b킷을 수정합니다.")
                    .clickRunCommand("/게임 킷 수정 ${kit.name}")
                    .build()
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

}