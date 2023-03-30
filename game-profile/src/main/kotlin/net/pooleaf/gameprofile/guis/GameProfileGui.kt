package net.pooleaf.gameprofile.guis

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryGui
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamehistory.GameHistoryApi
import net.pooleaf.permission.common.PermissionApi
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.time.format.DateTimeFormatter

class GameProfileGui(val commonPlayer: CommonPlayer<*>) : InventoryGui("${commonPlayer.displayName} §0님의 프로필", 3) {

    init {
        val isOnline = ChannelModule.isOnline(commonPlayer.uuid)

        // 배경
        val decoItemCode = if (isOnline) "160" else "160:7"
        val deco = ItemBuilder(decoItemCode)
            .displayName("§f")
            .build()

        for (x in 1..9) {
            mainPanel.set(x, 1, deco)
            mainPanel.set(x, 2, deco)
            mainPanel.set(x, 3, deco)
        }

        // 프로필
        val profileIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                val builder = ItemBuilder(Material.SKULL_ITEM)
                    .displayName("§f§l${commonPlayer.displayName}")

                if (isOnline) {
                    builder.lore("§f${ChannelModule.getChannelHasPlayer(commonPlayer.uuid)?.displayName ?: "알 수 없음"} §e채널에 접속 중입니다.")
                } else {
                    builder.lore("§e마지막 접속: §f${commonPlayer.lastOnline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss"))}")
                }

                builder.skull(commonPlayer.name)

                return builder.build()
            }
        }

        // 레벨
        val levelIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.EXP_BOTTLE)
                    .displayName("§e§l레벨 §f§l1")
                    .build()
            }
        }

        // 등급
        val rankIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                val permissionPlayer = PermissionApi.getPlayer(commonPlayer.uuid)
                if (permissionPlayer?.hasGroup() != true) return deco

                return getRankItem(permissionPlayer.groupName) ?: deco
            }
        }

        // 전적 버튼
        val historyIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                val gamePlayerStatsDto = GameHistoryApi.unsafe.sqlManager.gameDao.selectGamePlayerStatsByPlayerUuidAndGameTypeId(
                    commonPlayer.uuid.toString(),
                    100 // Ability
                )

                if (gamePlayerStatsDto == null) {
                    return ItemBuilder(Material.BOOK)
                        .displayName("§c§l전적")
                        .lore("§f전적이 없습니다.")
                        .build()
                } else {
                    return ItemBuilder(Material.BOOK)
                        .displayName("§c§l전적")
                        .lore("§e킬: §f${gamePlayerStatsDto.killCount}")
                        .lore("§e데스: §f${gamePlayerStatsDto.deathCount}")
                        // .lore("§f${gamePlayerStatsDto.assistCount} §e어시스트")
                        .lore("§e우승: §f${gamePlayerStatsDto.winCount}")
                        .lore("§f")
                        .lore("§f클릭 시 게임 기록을 확인합니다.")
                        .build()
                }
            }

            override fun onClick(event: InventoryGuiClickEvent) {
                GameHistoryGui(commonPlayer).open(event.player)
            }
        }

        // 아이콘 배치
        mainPanel.set(2, 2, profileIcon)
        mainPanel.set(3, 2, levelIcon)
        mainPanel.set(4, 2, rankIcon)
        mainPanel.set(7, 2, historyIcon)

        updateAsynchronously()
    }

    private fun getRankItem(rankName: String): ItemStack? {
        when (rankName.lowercase()) {
            "bronze" -> return ItemBuilder(Material.CLAY_BRICK)
                .displayName("§c§l브론즈")
                .build()
            "silver" -> return ItemBuilder(Material.IRON_INGOT)
                .displayName("§f§l실버")
                .build()
            "gold" -> return ItemBuilder(Material.GOLD_INGOT)
                .displayName("§e§l골드")
                .build()
            "platinum" -> return ItemBuilder(Material.EMERALD)
                .displayName("§a§l플래티넘")
                .build()
            "diamond" -> return ItemBuilder(Material.DIAMOND)
                .displayName("§b§l다이아몬드")
                .build()
            "master" -> return ItemBuilder("322") // 황금사과
                .displayName("§a§l마스터")
                .build()
            "challenger" -> return ItemBuilder("322:1") // 인챈트 황금사과
                .displayName("§e§l챌린저")
                .build()
        }

        return null
    }

}