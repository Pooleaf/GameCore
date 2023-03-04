package net.pooleaf.gamecore.startitem

import net.pooleaf.core.modules.annoconfig.AnnoConfigModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer
import org.bukkit.entity.Player
import java.io.File

class StartItemService {

    val file = File(GameCore.gamePlugin.dataFolder, "start-item-config.yml")


    /**
     * 시작 아이템을 저장합니다.
     */
    fun saveStartItemConfig() {
        AnnoConfigModule.save(file, GameCore.unsafe.startItemManager.startItem)
    }

    /**
     * 시작 아이템을 불러옵니다.
     */
    fun loadStartItemConfig() {
        AnnoConfigModule.load(file, GameCore.unsafe.startItemManager.startItem)
    }

    /**
     * 플레이어에게 시작 아이템을 지급합니다.
     * 온라인 플레이어에게만 사용할 수 있습니다.
     */
    fun giveStartItem(gamePlayer: GamePlayer) {
        if (!gamePlayer.isOnline) error("gamePlayer is not online")
        if (gamePlayer.isReceiveStartItems) error("gamePlayer already receive start items.")

        val startItem = GameCore.unsafe.startItemManager.startItem

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