package net.pooleaf.gamecore.commands

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
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import net.pooleaf.gamecore.supply.Supply
import net.pooleaf.gamecore.supply.SupplyBlock
import net.pooleaf.gamecore.supply.SupplyEditGui
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SupplyCommand {

    @Command(
        parent = ["", "게임"],
        name = ["보급품", "supply"],
        description = "보급품 명령어를 확인합니다.",
        helpCommand = true
    )
    fun supply(sender: CommonCommandSender<CommandSender>, result: HelpCommandResult) {
    }

    @Command(
        parent = ["보급품", "게임 보급품"],
        name = ["기록", "history"],
        arguments = "(페이지)",
        description = "보급품 기록을 확인합니다."
    )
    fun supply_history(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!GameCore.game.isGameStarted) {
            sender.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        val createdSupplies = GameCore.unsafe.supplyManager.createdSupply.reversed()
        if (createdSupplies.isEmpty()) {
            sender.sendWarning("아직 생성된 보급품이 없습니다.")
            return
        }

        object : PageableCommand<SupplyBlock>(result.entered, createdSupplies, 7) {
            override fun getHeaderColor(): CommonChatColor {
                return CommonChatColor.AQUA
            }

            override fun getHeaderMessage(): String {
                return "보급품 생성 기록"
            }

            override fun handleValue(supplyBlock: SupplyBlock, i: Int): Any {
                return "$headerColor[ ${createdSupplies.size - i} ] §bX: §f${supplyBlock.location.x.toInt()}§b, Y: §f${supplyBlock.location.y.toInt()}§b, Z: §f${supplyBlock.location.z.toInt()}"
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

    @Command(
        parent = ["보급품", "게임 보급품"],
        name = ["생성", "create"],
        arguments = "<보급품이름>",
        description = "보급품을 생성합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun supply_create(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (result.argumentsLength > 1) {
            sender.sendWarning("보급품 이름은 띄어쓰기가 불가능합니다.")
            return
        }

        if (GameCore.unsafe.supplyManager.exists(result.getArgument(0))) {
            sender.sendWarning("이미 존재하는 보급품 이름입니다.")
            return
        }

        val supply = Supply()
        supply.name = result.getArgument(0)
        supply.saveSupplyConfig()
        GameCore.unsafe.supplyManager.set(supply.name, supply)

        sender.sendMessage("${supply.name} §b보급품을 생성했습니다.")
    }

    @Command(
        parent = ["보급품", "게임 보급품"],
        name = ["소환", "spawn"],
        arguments = "<보급품이름>",
        description = "현재 위치에 보급품을 소환합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun supply_spawn(player: CommonPlayer<Player>, result: CommandResult) {
        if (!GameCore.game.isGameStarted) {
            player.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        val supplyName = result.getArgument(0)
        if (!GameCore.unsafe.supplyManager.exists(supplyName)) {
            player.sendWarning("존재하지 않는 보급품입니다.")
            return
        }

        val supply = GameCore.unsafe.supplyManager.get(supplyName)
        val location = player.platformSender.location
        BukkitSyncScope.launch {
            GameCore.unsafe.supplyService.createSupply(supply, location)
        }
    }

    @Command(
        parent = ["보급품", "게임 보급품"],
        name = ["랜덤소환", "spawnRandom"],
        arguments = "(보급품이름)",
        description = "현재 맵의 랜덤 위치에 보급품을 소환합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun supply_spawnRandom(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!GameCore.game.isGameStarted) {
            sender.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        val supply = if (result.argumentsLength > 0) {
            val supplyName = result.getArgument(0)
            if (!GameCore.unsafe.supplyManager.exists(supplyName)) {
                sender.sendWarning("존재하지 않는 보급품입니다.")
                return
            }

            GameCore.unsafe.supplyManager.get(supplyName)
        } else {
            GameCore.unsafe.supplyManager.getRandomSupply()
        }

        if (supply == null) {
            sender.sendWarning("생성된 보급품이 없습니다.")
            return
        }

        BukkitSyncScope.launch {
            GameCore.unsafe.supplyService.createSupplyRandomLocation(supply)
        }
    }

    @Command(
        parent = ["보급품", "게임 보급품"],
        name = ["수정", "edit"],
        arguments = "<보급품이름>",
        description = "보급품의 아이템을 수정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun supply_edit(player: CommonPlayer<Player>, result: CommandResult) {
        val supplyName = result.getArgument(0)

        if (!GameCore.unsafe.supplyManager.exists(supplyName)) {
            player.sendWarning("존재하지 않는 보급품입니다.")
            return
        }

        val supply = GameCore.unsafe.supplyManager.get(supplyName)
        SupplyEditGui(supply, player.platformSender).open(player.platformSender)
    }

    @Command(
        parent = ["보급품", "게임 보급품"],
        name = ["삭제", "delete"],
        arguments = "<보급품이름>",
        description = "보급품을 삭제합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun supply_delete(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val supplyName = result.getArgument(0)

        if (!GameCore.unsafe.supplyManager.exists(supplyName)) {
            sender.sendWarning("존재하지 않는 보급품입니다.")
            return
        }

        GameCore.unsafe.supplyManager.remove(supplyName)
        GameCore.unsafe.supplyService.deleteSupplyConfig(supplyName)

        sender.sendMessage("${supplyName} §b보급품을 삭제했습니다.")
    }

    @Command(
        parent = ["보급품", "게임 보급품"],
        name = ["목록", "list"],
        arguments = "(페이지)",
        description = "보급품 목록을 확인합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun supply_list(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val supplies = GameCore.unsafe.supplyManager.values().toList()
        if (supplies.isEmpty()) {
            sender.sendWarning("생성된 보급품이 없습니다.")
            return
        }

        object : PageableCommand<Supply>(result.entered, supplies, 7) {
            override fun getHeaderColor(): CommonChatColor {
                return CommonChatColor.AQUA
            }

            override fun getHeaderMessage(): String {
                return "보급품 목록"
            }

            override fun handleValue(supply: Supply, i: Int): Any {
                return SimpleComponentBuilder("$headerColor[ ${i + 1} ] §f${supply.name} §b/ 확률 비율: §f${supply.probabilityRatio}")
                    .hoverShowText("§b클릭 시 §f${supply.name} §b보급품을 수정합니다.")
                    .clickRunCommand("/게임 보급품 수정 ${supply.name}")
                    .build()
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

}
