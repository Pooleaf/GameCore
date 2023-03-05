package net.pooleaf.gamereplay.data.records.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.block.BlockChangeInfo
import net.pooleaf.gamereplay.data.datas.block.MultiBlockChangeData

class MultiBlockChangeDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.MULTI_BLOCK_CHANGE) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet

        val chunk = packet.chunkCoordIntPairs.read(0)
        val multiBlockChangeInfos = packet.multiBlockChangeInfoArrays.read(0)
        val blockChangeInfos = multiBlockChangeInfos.map {
            BlockChangeInfo().apply {
                x = it.x
                y = it.y
                z = it.z
                blockTypeId = it.data.type.id
                blockData = it.data.data
            }
        }.toList()

        val recordData = MultiBlockChangeData().apply {
            chunkX = chunk.chunkX
            chunkZ = chunk.chunkZ
            this.blockChangeInfos = blockChangeInfos
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}