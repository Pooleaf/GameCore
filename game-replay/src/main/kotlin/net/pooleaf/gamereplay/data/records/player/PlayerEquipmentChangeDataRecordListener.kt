package net.pooleaf.gamereplay.data.records.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerEquipmentChangeData
import net.pooleaf.gamereplay.events.RecordStopEvent
import net.pooleaf.gamereplay.events.RecordTickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

class PlayerEquipmentChangeDataRecordListener : Listener {

    val beforeHands = hashMapOf<Player, ItemStack>()
    val beforeHelmets = hashMapOf<Player, ItemStack>()
    val beforeChestplates = hashMapOf<Player, ItemStack>()
    val beforeLeggingses = hashMapOf<Player, ItemStack>()
    val beforeBootses = hashMapOf<Player, ItemStack>()


    @EventHandler
    fun onRecordTick(event: RecordTickEvent) {
        event.record.recordTargetPlayers.forEach { uuid ->
            val player = Bukkit.getPlayer(uuid)
            if (player == null) return@forEach

            val beforeHand = beforeHands.get(player)
            if (beforeHand != player.inventory.itemInHand) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 0
                    item = player.inventory.itemInHand
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeHands.put(player, player.inventory.itemInHand)

            val beforeHelmet = beforeHelmets.get(player)
            if (beforeHelmet != player.inventory.helmet) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 4
                    item = player.inventory.helmet
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeHelmets.put(player, player.inventory.helmet)

            val beforeChestplate = beforeChestplates.get(player)
            if (beforeChestplate != player.inventory.chestplate) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 3
                    item = player.inventory.chestplate
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeChestplates.put(player, player.inventory.chestplate)

            val beforeLeggings = beforeLeggingses.get(player)
            if (beforeLeggings != player.inventory.leggings) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 2
                    item = player.inventory.leggings
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeLeggingses.put(player, player.inventory.leggings)

            val beforeBoots = beforeBootses.get(player)
            if (beforeBoots != player.inventory.boots) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 1
                    item = player.inventory.boots
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeBootses.put(player, player.inventory.boots)
        }
    }

    @EventHandler
    fun onRecordStop(event: RecordStopEvent) {
        beforeHelmets.clear()
        beforeChestplates.clear()
        beforeLeggingses.clear()
        beforeBootses.clear()
    }

}