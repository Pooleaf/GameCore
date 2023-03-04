package net.pooleaf.gamereplay.data.player

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import java.util.*

/**
 * Entity Index 0만 녹화함
 * https://wiki.vg/index.php?title=Entity_metadata&oldid=7415#Entity
 */
class PlayerMetaDataData(
    var playerUuid: UUID? = null,
    var index: Int = 0,
    var value: Byte = 0
) : RecordData {

    override val type: String = "playerMetaData"

}

class PlayerMetaDataDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_METADATA) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet
        val entityId = packet.integers.read(0)
        val entity = packet.getEntityModifier(event.player.world).read(0)

        // 본인 것만 녹화
        if (entityId != entity.entityId || entityId != event.player.entityId) return

        val entityMetaData = packet.watchableCollectionModifier.read(0)
        if (entityMetaData.isEmpty()) return

        val packetIndex = entityMetaData.get(0).index
        val packetValue = entityMetaData.get(0).value

        if (packetIndex != 0) return

        val recordData = PlayerMetaDataData().apply {
            playerUuid = event.player.uniqueId
            index = packetIndex
            value = packetValue as Byte
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class PlayerMetaDataDataReplayHandler : RecordDataReplayHandler<PlayerMetaDataData> {

    override fun onPlay(recordData: PlayerMetaDataData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        // 불 처리
        when (recordData.value % 2) {
            0 -> citizensNpc.entity.fireTicks = 0
            1 -> citizensNpc.entity.fireTicks = 9999999
        }

        val entityPlayer = BukkitReflectionUtil.getHandle(citizensNpc.entity) as EntityPlayer
        val dataWatcher = entityPlayer.dataWatcher
        dataWatcher.watch(recordData.index, recordData.value)

        val packet = PacketPlayOutEntityMetadata(citizensNpc.entity.entityId, dataWatcher, false)
        BukkitReflectionUtil.sendPacket(replayPlayer.viewer, packet)
    }

}