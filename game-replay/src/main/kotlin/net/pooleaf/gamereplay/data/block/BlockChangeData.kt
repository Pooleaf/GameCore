package net.pooleaf.gamereplay.data.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

data class BlockChangeData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var blockTypeId: Int = 0,
    var blockData: Byte = 0
) : RecordData {

    override val type: String = "blockChange"

}

class BlockChangeDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.BLOCK_CHANGE) {

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

class BlockChangeDataReplayHandler : RecordDataReplayHandler<BlockChangeData> {

    override fun onPlay(recordData: BlockChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(recordData.x, recordData.y, recordData.z)!!
        virtualBlock.typeId = recordData.blockTypeId
        virtualBlock.typeData = recordData.blockData

        virtualBlock.showTo(viewer)
    }

}