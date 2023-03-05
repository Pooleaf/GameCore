package net.pooleaf.gamereplay.data.replays.player

import net.citizensnpcs.api.trait.trait.Equipment
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerEquipmentChangeData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player

class PlayerEquipmentChangeDataReplayHandler : RecordDataReplayHandler<PlayerEquipmentChangeData> {

    override fun onPlay(recordData: PlayerEquipmentChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return
        if (citizensNpc.entity == null) return

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