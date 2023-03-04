package net.pooleaf.gamereplay.data.player

import net.citizensnpcs.api.trait.trait.Equipment
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.events.RecordStopEvent
import net.pooleaf.gamereplay.events.RecordTickEvent
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*

data class PlayerEquipmentChangeData(
    var playerUuid: UUID? = null,
    var equipmentType: Int = 0,
    var item: ItemStack? = null
) : RecordData {

    override val type: String = "playerEquipmentChange"

}

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

class PlayerEquipmentChangeDataReplayHandler : RecordDataReplayHandler<PlayerEquipmentChangeData> {

    override fun onPlay(recordData: PlayerEquipmentChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        val equipmentSlot = when(recordData.equipmentType) {
            0 -> Equipment.EquipmentSlot.HAND
            4 -> Equipment.EquipmentSlot.HELMET
            3 -> Equipment.EquipmentSlot.CHESTPLATE
            2 -> Equipment.EquipmentSlot.LEGGINGS
            1 -> Equipment.EquipmentSlot.BOOTS
            else -> return
        }

        citizensNpc.getOrAddTrait(Equipment::class.java).set(equipmentSlot, recordData.item)

        // 시티즌 API만 사용하면 즉시 반영이 안되므로 패킷으로 한번 더 보내줌
        val packet = PacketPlayOutEntityEquipment(citizensNpc.entity.entityId, recordData.equipmentType, CraftItemStack.asNMSCopy(recordData.item))
        BukkitReflectionUtil.sendPacket(replayPlayer.viewer, packet)

    }

}