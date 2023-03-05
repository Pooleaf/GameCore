package net.pooleaf.gamereplay.data.replays.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.EntityChangeBlockData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class EntityChangeBlockDataReplayHandler : RecordDataReplayHandler<EntityChangeBlockData> {

    override fun onPlay(recordData: EntityChangeBlockData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(recordData.x, recordData.y, recordData.z)!!
        virtualBlock.typeId = recordData.blockTypeId
        virtualBlock.typeData = recordData.blockData

        virtualBlock.showTo(viewer)
    }

}