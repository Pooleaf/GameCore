package net.pooleaf.gamecore.supply

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
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SupplyEditGui(
    val supply: Supply,
    val player: Player
) : InventoryGui(supply.name, 6) {

    val settingPanel: InventoryPanel
    val itemPanel: InventoryPanel

    init {
        settingPanel = createPanel("settingPanel", 1, 1, 9, 2)
        itemPanel = createPanel("itemPanel", 1, 3, 9, 4)

        // 구분선
        val decoIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.STAINED_GLASS_PANE)
                    .displayName("§f")
                    .build()
            }
        }

        for (x in 2..9) {
            settingPanel.set(x, 1, decoIcon)
        }

        for (x in 1..9) {
            settingPanel.set(x, 2, decoIcon)
        }

        // 확률 비율 아이콘
        val probabilityRatioIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.DIAMOND)
                    .displayName("§e§l확률 비율 §f${supply.probabilityRatio}")
                    .lore("§f클릭 시 확률 비율을 수정합니다.")
                    .build()
            }

            override fun onClick(event: InventoryGuiClickEvent) {
                // 표지판으로 입력
                object : SignGui("", "", "^^^^^^^^^^", "확률 비율을 입력해 주세요") {
                    override fun onSignComplete(player: Player, lines: Array<String>) {
                        // 레벨 설정
                        val probabilityRatio = lines[0].toIntOrNull()
                        if (probabilityRatio == null) {
                            player.sendWarning("확률 비율은 정수만 입력 가능합니다.")
                            return
                        }

                        supply.probabilityRatio = probabilityRatio
                        player.sendMessage("§b확률 비율을 §f${probabilityRatio}§b(으)로 설정했습니다.")

                        // GUI 열기
                        SupplyEditGui(supply, player).open(player)
                    }
                }.open(event.player)
            }
        }
        settingPanel.set(1, 1, probabilityRatioIcon)

        // 보급품 아이템 넣기
        supply.items.forEach { itemPanel.add(it) }

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
        supply.items.clear()
        itemPanel.itemListInInventory.forEach { supply.items.add(it as ItemStack) }

        supply.saveSupplyConfig()
        event.player.sendMessage("§b보급품 §f${supply.name}을(를) 저장했습니다.")
    }

    private fun calculateClickCancel(clickedPanel: InventoryPanel, x: Int, y: Int): Boolean {
        return clickedPanel == settingPanel && y == 1 && x != 1
    }

}