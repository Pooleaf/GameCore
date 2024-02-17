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
import java.util.UUID

class PlayerEquipmentChangeDataRecordListener : Listener {

    val beforeHands = hashMapOf<UUID, ItemStack>()
    val beforeHelmets = hashMapOf<UUID, ItemStack>()
    val beforeChestplates = hashMapOf<UUID, ItemStack>()
    val beforeLeggingses = hashMapOf<UUID, ItemStack>()
    val beforeBootses = hashMapOf<UUID, ItemStack>()


    @EventHandler
    fun onRecordTick(event: RecordTickEvent) {
        event.record.recordTargetPlayers.forEach { uuid ->
            val player = Bukkit.getPlayer(uuid)
            if (player == null) return@forEach

            val beforeHand = beforeHands.get(player.uniqueId)
            if (beforeHand != player.inventory.itemInHand) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 0
                    item = player.inventory.itemInHand
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeHands.put(player.uniqueId, player.inventory.itemInHand)

            val beforeHelmet = beforeHelmets.get(player.uniqueId)
            if (beforeHelmet != player.inventory.helmet) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 4
                    item = player.inventory.helmet
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeHelmets.put(player.uniqueId, player.inventory.helmet)

            val beforeChestplate = beforeChestplates.get(player.uniqueId)
            if (beforeChestplate != player.inventory.chestplate) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 3
                    item = player.inventory.chestplate
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeChestplates.put(player.uniqueId, player.inventory.chestplate)

            val beforeLeggings = beforeLeggingses.get(player.uniqueId)
            if (beforeLeggings != player.inventory.leggings) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 2
                    item = player.inventory.leggings
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeLeggingses.put(player.uniqueId, player.inventory.leggings)

            val beforeBoots = beforeBootses.get(player.uniqueId)
            if (beforeBoots != player.inventory.boots) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 1
                    item = player.inventory.boots
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeBootses.put(player.uniqueId, player.inventory.boots)
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