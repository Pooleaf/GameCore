package net.pooleaf.gamereplay.data.replays.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.MultiBlockChangeData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.virtual.block.VirtualBlock
import org.bukkit.entity.Player

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