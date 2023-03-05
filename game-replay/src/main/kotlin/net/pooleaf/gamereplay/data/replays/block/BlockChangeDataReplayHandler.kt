package net.pooleaf.gamereplay.data.replays.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.BlockChangeData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class BlockChangeDataReplayHandler : RecordDataReplayHandler<BlockChangeData> {

    override fun onPlay(recordData: BlockChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(recordData.x, recordData.y, recordData.z)!!
        virtualBlock.typeId = recordData.blockTypeId
        virtualBlock.typeData = recordData.blockData

        virtualBlock.showTo(viewer)
    }

}