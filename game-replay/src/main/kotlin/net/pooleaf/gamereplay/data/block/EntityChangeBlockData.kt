package net.pooleaf.gamereplay.data.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

data class EntityChangeBlockData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var blockTypeId: Int = 0,
    var blockData: Byte = 0
) : RecordData {

    override val type: String = "entityChangeBlock"

}

class EntityChangeBlockDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = EntityChangeBlockData().apply {
            x = event.block.x
            y = event.block.y
            z = event.block.z
            blockTypeId = event.to.id
            blockData = event.data
        }

        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class EntityChangeBlockDataReplayHandler : RecordDataReplayHandler<EntityChangeBlockData> {

    override fun onPlay(recordData: EntityChangeBlockData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(recordData.x, recordData.y, recordData.z)!!
        virtualBlock.typeId = recordData.blockTypeId
        virtualBlock.typeData = recordData.blockData

        virtualBlock.showTo(viewer)
    }

}