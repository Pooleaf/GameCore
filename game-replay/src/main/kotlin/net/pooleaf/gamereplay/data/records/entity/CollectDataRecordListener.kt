package net.pooleaf.gamereplay.data.records.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.entity.CollectData
import org.bukkit.Bukkit

class CollectDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.COLLECT) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val packetCollectedEntityId = packet.integers.read(0)
        val packetCollectorEntityId = packet.integers.read(1)
        val collectorPlayer = Bukkit.getOnlinePlayers().filter { it.entityId == packetCollectorEntityId }.firstOrNull()
        if (collectorPlayer == null) return

        val recordData = CollectData().apply {
            collectedEntityId = packetCollectedEntityId
            collectorPlayerUuid = collectorPlayer.uniqueId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}