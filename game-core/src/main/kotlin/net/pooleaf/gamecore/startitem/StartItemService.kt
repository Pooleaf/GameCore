package net.pooleaf.gamecore.startitem

import net.pooleaf.core.modules.annoconfig.AnnoConfigModule
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerStartItemReceiveEvent
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.permission.common.PermissionApi
import org.bukkit.Bukkit
import java.io.File

class StartItemService {

    val defaultStartItemName = "기본"

    val folder = File(GameCore.gamePlugin.dataFolder, "start-items").apply { mkdirs() }

    /**
     * 시작 아이템 설정 파일을 반환합니다.
     */
    fun getStartItemConfigFile(rankName: String): File {
        return File(folder, "${rankName}.yml")
    }

    /**
     * 시작 아이템을 저장합니다.
     */
    fun saveStartItemConfig(rankName: String) {
        val file = getStartItemConfigFile(rankName)
        val startItem = GameCore.unsafe.startItemManager.get(rankName) ?: return
        AnnoConfigModule.save(file, startItem)

        Logger.log("${rankName} 시작아이템을 저장했습니다.")
    }

    /**
     * 시작 아이템을 불러옵니다.
     */
    fun loadStartItemConfig(rankName: String) {
        val file = getStartItemConfigFile(rankName)
        if (!file.exists()) return

        val startItem = StartItem()
        AnnoConfigModule.load(file, startItem)
        GameCore.unsafe.startItemManager.set(rankName, startItem)

        Logger.log("${rankName} 시작아이템을 불러왔습니다.")
    }

    /**
     * 모든 시작 아이템을 불러옵니다.
     */
    fun loadAllStartItemConfig() {
        folder.listFiles().filter { it.name.endsWith(".yml") }
            .forEach {
                try {
                    loadStartItemConfig(it.name.substringBefore(".yml"))
                } catch (exception: Exception) {
                    Logger.warning("${it.name} 시작아이템을 불러올 수 없습니다.")
                    exception.printStackTrace()
                }
            }
    }

    /**
     * 시작 아이템을 삭제합니다.
     */
    fun deleteStartItemConfig(rankName: String) {
        val file = getStartItemConfigFile(rankName)
        file.delete()
    }

    /**
     * 플레이어에게 시작 아이템을 지급합니다.
     * 기본 시작 아이템을 먼저 지급한 후 등급 시작 아이템을 지급합니다.
     * 온라인 플레이어에게만 사용할 수 있습니다.
     */
    fun giveStartItem(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) error("gamePlayer is not online")
        if (gamePlayer.isReceiveStartItems) error("gamePlayer already receive start items.")

        var startItem = StartItem()
        val defaultStartItem = GameCore.unsafe.startItemManager.get(defaultStartItemName)
        if (defaultStartItem != null) {
            startItem.helmetItem = defaultStartItem.helmetItem
            startItem.chestplatItem = defaultStartItem.chestplatItem
            startItem.leggingsItem = defaultStartItem.leggingsItem
            startItem.bootsItem = defaultStartItem.bootsItem
            startItem.items.addAll(defaultStartItem.items)
            startItem.level = defaultStartItem.level
        }

        val permissionPlayer = PermissionApi.getPlayer(gamePlayer.uuid)
        val permissionGroupName = permissionPlayer?.groupName

        val rankStartItem = GameCore.unsafe.startItemManager.get(permissionGroupName)
        if (rankStartItem != null) {
            if (rankStartItem.helmetItem != null) startItem.helmetItem = rankStartItem.helmetItem
            if (rankStartItem.chestplatItem != null) startItem.chestplatItem = rankStartItem.chestplatItem
            if (rankStartItem.leggingsItem != null) startItem.leggingsItem = rankStartItem.leggingsItem
            if (rankStartItem.bootsItem != null) startItem.bootsItem = rankStartItem.bootsItem
            startItem.items.addAll(rankStartItem.items)
            if (rankStartItem.level != 0) startItem.level = rankStartItem.level
        }

        // 이벤트
        val event = GamePlayerStartItemReceiveEvent(gamePlayer, startItem)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return

        // 이벤트에서 수정한 시작아이템 사용
        startItem = event.startItem

        // 시작아이템 지급
        val player = gamePlayer.player
        player.inventory.helmet = startItem.helmetItem
        player.inventory.chestplate = startItem.chestplatItem
        player.inventory.leggings = startItem.leggingsItem
        player.inventory.boots = startItem.bootsItem
        startItem.items.forEach { player.inventory.addItem(it) }
        player.level = startItem.level

        gamePlayer.isReceiveStartItems = true
    }

}