package net.pooleaf.gamereplay.data.records.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.data.datas.block.BlockChangeData

class BlockChangeDataRecordListener : PacketAdapter(GameReplayPlugin.instance, PacketType.Play.Server.BLOCK_CHANGE) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val position = event.packet.blockPositionModifier.read(0)
        val packetBlockData = event.packet.blockData.read(0)

        val recordData = BlockChangeData().apply {
            x = position.x
            y = position.y
            z = position.z
            blockTypeId = packetBlockData.type.id
            blockData = packetBlockData.data.toByte()
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}