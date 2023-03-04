package net.pooleaf.gamecore.startitem

import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryGui
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryGuiClickAction
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryPanel
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiCloseEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiPlayerInventoryClickEvent
import net.pooleaf.core.modules.gui.bukkit.sign.SignGui
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class StartItemEditGui : InventoryGui("시작 아이템 수정", 6) {

    val armorPanel: InventoryPanel
    val itemPanel: InventoryPanel

    init {
        armorPanel = createPanel("armorPanel", 1, 1, 9, 2)
        itemPanel = createPanel("itemPanel", 1, 3, 9, 4)

        // 구분선
        val decoIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.STAINED_GLASS_PANE)
                    .displayName("§f")
                    .build()
            }
        }

        for (x in 5..8) {
            armorPanel.set(x, 1, decoIcon)
        }

        for (x in 1..9) {
            armorPanel.set(x, 2, decoIcon)
        }

        // 갑옷 아이템 넣기
        val startItem = GameCore.unsafe.startItemManager.startItem

        armorPanel.set(1, 1, startItem.helmetItem)
        armorPanel.set(2, 1, startItem.chestplatItem)
        armorPanel.set(3, 1, startItem.leggingsItem)
        armorPanel.set(4, 1, startItem.bootsItem)

        // 시작 아이템 넣기
        startItem.items.forEach { itemPanel.add(it) }

        // 레벨
        val levelIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.EXP_BOTTLE)
                    .displayName("§f${startItem.level} §e§l레벨")
                    .lore("§f클릭 시 레벨을 수정합니다.")
                    .build()
            }

            override fun onClick(event: InventoryGuiClickEvent) {
                // 표지판으로 입력
                object : SignGui("", "", "^^^^^^^^^^", "레벨을 입력해 주세요") {
                    override fun onSignComplete(player: Player, lines: Array<String>) {
                        // 레벨 설정
                        val level = lines[0].toIntOrNull()
                        if (level == null) {
                            player.sendWarning("레벨은 정수만 입력 가능합니다.")
                            return
                        }

                        GameCore.unsafe.startItemManager.startItem.level = level
                        player.sendMessage("§b시작 레벨을 §f${level}§b(으)로 설정했습니다.")

                        // GUI 열기
                        StartItemEditGui().open(player)
                    }
                }.open(event.player)
            }
        }
        armorPanel.set(9, 1, levelIcon)

        updateAsynchronously()
    }

    override fun onClick(event: InventoryGuiClickEvent) {
        when (event.clickAction) {
            InventoryGuiClickAction.LEFT_PLACE,
            InventoryGuiClickAction.LEFT_HOLD,
            InventoryGuiClickAction.RIGHT_PLACE,
            InventoryGuiClickAction.RIGHT_HOLD -> event.isCancelled = calculateClickCancel(event.clickedPanel, event.x, event.y)
            else -> event.isCancelled = true
        }
    }

    override fun onPlayerInventoryClick(event: InventoryGuiPlayerInventoryClickEvent) {
        when (event.clickAction) {
            InventoryGuiClickAction.LEFT_PLACE,
            InventoryGuiClickAction.LEFT_HOLD,
            InventoryGuiClickAction.RIGHT_PLACE,
            InventoryGuiClickAction.RIGHT_HOLD -> event.isCancelled = false
            else -> event.isCancelled = true
        }
    }

    override fun onClose(event: InventoryGuiCloseEvent) {
        val startItem = GameCore.unsafe.startItemManager.startItem

        // 갑옷
        startItem.helmetItem = armorPanel.getItemInInventory(1, 1)
        startItem.chestplatItem = armorPanel.getItemInInventory(2, 1)
        startItem.leggingsItem = armorPanel.getItemInInventory(3, 1)
        startItem.bootsItem = armorPanel.getItemInInventory(4, 1)

        // 아이템
        startItem.items.clear()
        itemPanel.itemListInInventory.forEach { startItem.items.add(it as ItemStack) }

        startItem.saveStartItemConfig()
        event.player.sendMessage("§b시작 아이템을 저장했습니다.")
    }

    private fun calculateClickCancel(clickedPanel: InventoryPanel, x: Int, y: Int): Boolean {
        // 갑옷 & 레벨
        if (clickedPanel == armorPanel && y == 1) {
            when (x) {
                in 1..4 -> return false
                9 -> return false
            }
        }

        // 아이템 패널
        if (clickedPanel == itemPanel) {
            return false
        }

        return true
    }

}