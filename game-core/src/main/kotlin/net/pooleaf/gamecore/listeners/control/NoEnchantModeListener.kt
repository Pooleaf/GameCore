package net.pooleaf.gamecore.listeners.control

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerJoinToGameEvent
import net.pooleaf.gamecore.utils.hasEnchantment
import net.pooleaf.gamecore.utils.removeEnchantmentAll
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent

class NoEnchantModeListener : Listener {

    @EventHandler
    fun onJoin(event: GamePlayerJoinToGameEvent) {
        if (!GameCore.game.isGameStarted || !GameCore.game.isNoEnchantMode) return

        val player = event.gamePlayer.player
        player.itemOnCursor = player.itemOnCursor?.removeEnchantmentAll()
        player.inventory.armorContents.filterNotNull().forEach { it.removeEnchantmentAll() }
        player.inventory.filterNotNull().forEach { it.removeEnchantmentAll() }
    }

    @EventHandler
    fun onPickupItem(event: PlayerPickupItemEvent) {
        if (!GameCore.game.isGameStarted || !GameCore.game.isNoEnchantMode) return

        val gamePlayer = event.player.toGamePlayer() ?: return
        if (!gamePlayer.isPlaying()) return

        if (event.item.itemStack.hasEnchantment()) {
            event.item.itemStack = event.item.itemStack.removeEnchantmentAll()
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        if (!GameCore.game.isGameStarted || !GameCore.game.isNoEnchantMode) return

        val gamePlayer = event.player.toGamePlayer() ?: return
        if (!gamePlayer.isPlaying()) return

        if (event.itemDrop.itemStack.hasEnchantment()) {
            event.itemDrop.itemStack = event.itemDrop.itemStack.removeEnchantmentAll()
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (!GameCore.game.isGameStarted || !GameCore.game.isNoEnchantMode) return

        val gamePlayer = (event.whoClicked as Player).toGamePlayer() ?: return
        if (!gamePlayer.isPlaying()) return

        if (event.currentItem?.hasEnchantment() == true) {
            event.currentItem = event.currentItem?.removeEnchantmentAll()
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!GameCore.game.isGameStarted || !GameCore.game.isNoEnchantMode) return

        val gamePlayer = event.player.toGamePlayer() ?: return
        if (!gamePlayer.isPlaying()) return

        if (event.player.itemInHand?.hasEnchantment() == true) {
            event.player.itemInHand = event.player.itemInHand?.removeEnchantmentAll()
        }
    }

}