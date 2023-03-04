package net.pooleaf.gamecore.commands

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.core.modules.support.common.pageable.PageableCommand
import net.pooleaf.core.plugin.CorePlugin
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import net.pooleaf.gamecore.map.GameMap
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MapCommand {

    @Command(
        parent = ["", "게임"],
        name = ["맵", "map"],
        helpCommand = true,
        description = "맵 명령어를 확인합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map(sender: CommonCommandSender<CommandSender>, result: HelpCommandResult) {
    }


    @Command(
        parent = ["맵", "게임 맵"],
        name = ["스폰설정", "setSpawn"],
        description = "현재 위치 스폰 위치로 설정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_setSpawn(player: CommonPlayer<Player>, result: CommandResult) {
        GameCore.spawnConfig.spawnLocation = player.platformSender.location
        GameCore.spawnConfig.save()

        player.sendMessage("§b현재 위치를 스폰 위치로 설정했습니다.")
    }

    @Command(
        parent = ["맵", "게임 맵"],
        name = ["생성", "create"],
        arguments = "<맵이름>",
        description = "현재 위치를 중앙으로한 맵을 생성합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_create(player: CommonPlayer<Player>, result: CommandResult) {
        if (result.argumentsLength > 1) {
            player.sendWarning("맵 이름은 띄어쓰기가 불가능합니다.")
            return
        }

        if (GameCore.unsafe.mapManager.exists(result.getArgument(0))) {
            player.sendWarning("이미 존재하는 맵 이름입니다.")
            return
        }

        val map = GameCore.unsafe.mapManager.gameMapFactory.createGameMap()
        map.name = result.getArgument(0)
        map.centerLocation = player.platformSender.location
        map.saveMapConfig()
        GameCore.unsafe.mapManager.set(map.name, map)

        player.sendMessage("${map.name} §b맵을 생성했습니다.")
    }

    @Command(
        parent = ["맵", "게임 맵"],
        name = ["표기설정", "setDisplayName"],
        arguments = "<맵이름> <표기이름>",
        description = "해당 맵의 표기 이름을 설정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_setDisplayName(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val map = GameCore.unsafe.mapManager.get(result.enteredArguments)
        if (map == null) {
            sender.sendWarning("존재하지 않는 맵입니다.")
            return
        }

        val newDisplayName = ChatColor.translateAlternateColorCodes('&', result.subArgument(1))
        map.displayName = newDisplayName
        map.saveMapConfig()

        sender.sendMessage("${map.name} §b맵의 표기 이름을 §f${map.displayName} §b(으)로 설정했습니다.")
    }

    @Command(
        parent = ["맵", "게임 맵"],
        name = ["위치설정", "setLocation"],
        arguments = "<맵이름>",
        description = "현재 위치를 맵의 중앙으로 설정합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_setLocation(player: CommonPlayer<Player>, result: CommandResult) {
        val map = GameCore.unsafe.mapManager.get(result.getArgument(0))
        if (map == null) {
            player.sendWarning("존재하지 않는 맵입니다.")
            return
        }

        map.centerLocation = player.platformSender.location
        map.saveMapConfig()

        player.sendMessage("§b현재 위치를 §f${map.name} §b맵의 중앙 위치로 설정했습니다.")
    }

    @Command(
        parent = ["맵", "게임 맵"],
        name = ["범위설정", "setRadius", "setRange"],
        arguments = "<맵이름> <반지름>",
        description = "현재 위치를 중앙으로한 맵을 생성합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_setRadius(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val map = GameCore.unsafe.mapManager.get(result.getArgument(0))
        if (map == null) {
            sender.sendWarning("존재하지 않는 맵입니다.")
            return
        }

        val radius = result.getArgumentAsInt(result.argumentsLength - 1)
        if (radius == null) {
            sender.sendWarning("범위는 정수만 입력할 수 있습니다.")
            return
        }

        map.worldBorderSize = radius
        map.saveMapConfig()

        sender.sendMessage("${map.name} §b맵의 범위를 §f${radius}§b로 설정했습니다.")
    }

    @Command(
        parent = ["맵", "게임 맵"],
        name = ["삭제", "delete"],
        arguments = "<맵이름>",
        description = "맵을 삭제합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_delete(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val map = GameCore.unsafe.mapManager.get(result.subArgument(0))
        if (map == null) {
            sender.sendWarning("존재하지 않는 맵입니다.")
            return
        }

        map.deleteMapConfig()
        GameCore.unsafe.mapManager.remove(map.name)

        sender.sendMessage("${map.name} §b맵을 삭제했습니다.")
    }

    @Command(
        parent = ["맵", "게임 맵"],
        name = ["목록", "list"],
        arguments = "(페이지)",
        description = "맵 목록을 확인합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_list(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val maps = GameCore.unsafe.mapManager.values().toList()
        if (maps.isEmpty()) {
            sender.sendWarning("생성된 맵이 없습니다.")
            return
        }

        object : PageableCommand<GameMap>(result.entered, maps, 7) {
            override fun getHeaderColor(): CommonChatColor {
                return (GameCore.gamePlugin as CorePlugin).color
            }

            override fun getHeaderMessage(): String {
                return "맵 목록"
            }

            override fun handleValue(map: GameMap, i: Int): Any {
                return SimpleComponentBuilder("$headerColor[ $i ] §f${map.name}")
                    .hoverShowText(
                        """
                            ${map.name}
                            
                            §a좌표
                            §f${map.getCenterLocationString()}
                            
                            §b범위
                            §f${map.worldBorderSize}
                        """.trimIndent()
                    )
                    .build()
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

    @Command(
        parent = ["맵", "게임 맵"],
        name = ["이동", "teleport", "tp"],
        arguments = "<맵이름>",
        description = "맵으로 텔레포트합니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun map_teleport(player: CommonPlayer<Player>, result: CommandResult) {
        val map = GameCore.unsafe.mapManager.get(result.getArgument(0))
        if (map == null) {
            player.sendWarning("존재하지 않는 맵입니다.")
            return
        }

        BukkitAsyncScope.launch {
            // 맵이 로딩 되어 있지 않으면 로딩
            if (!map.isWorldLoaded()) {
                player.sendMessage("${map.name} §b맵을 불러오는 중입니다..")
                map.loadWorld()
                player.sendMessage("${map.name} §b맵을 불러왔습니다.")
            }

            // 텔레포트
            val centerLocation = map.centerLocation
            if (centerLocation == null) {
                player.sendWarning("맵의 중앙 위치가 설정되지 않았습니다.")
                return@launch
            }

            TeleportUtil.teleport(player.platformSender, centerLocation)
            player.sendMessage("${map.name} §b맵으로 텔레포트했습니다.")
        }
    }

}