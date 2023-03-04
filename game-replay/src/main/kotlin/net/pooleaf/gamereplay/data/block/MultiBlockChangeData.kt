package net.pooleaf.gamereplay.data.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.virtual.block.VirtualBlock
import org.bukkit.entity.Player
import org.bukkit.event.Listener

data class MultiBlockChangeData(
    var chunkX: Int = 0,
    var chunkZ: Int = 0,
    var blockChangeInfos: List<BlockChangeInfo> = arrayListOf()
) : RecordData {

    override val type: String = "multiBlockChange"

}

class BlockChangeInfo() {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var blockTypeId: Int = 0
    var blockData: Int = 0
}

class MultiBlockChangeDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.MULTI_BLOCK_CHANGE) {

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

class MultiBlockChangeDataReplayHandler : RecordDataReplayHandler<MultiBlockChangeData> {

    override fun onPlay(recordData: MultiBlockChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualBlocks = arrayListOf<VirtualBlock>()
        recordData.blockChangeInfos.forEach { blockChangeInfo ->
            val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(blockChangeInfo.x, blockChangeInfo.y, blockChangeInfo.z)!!
            virtualBlock.typeId = blockChangeInfo.blockTypeId
            virtualBlock.typeData = blockChangeInfo.blockData.toByte()

            virtualBlocks.add(virtualBlock)
        }

        replayPlayer.virtualBlockManager.showToBulk(virtualBlocks, viewer)
    }

}