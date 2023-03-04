package net.pooleaf.gamereplay.data.block

import com.cryptomorin.xseries.XSound
import net.minecraft.server.v1_8_R3.MathHelper
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.virtual.block.VirtualBlock
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import kotlin.random.Random


data class EntityExplodeData(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yield: Float = 0.0F,
    var blockInfos: List<BlockExplodeInfo> = arrayListOf()
) : RecordData {

    override val type: String = "entityExplode"

}

data class BlockExplodeInfo(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0
) {
}

class EntityExplodeDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val blockInfos = event.blockList().map { block ->
            BlockExplodeInfo().apply {
                x = block.x
                y = block.y
                z = block.z
            }
        }

        val recordData = EntityExplodeData().apply {
            x = event.entity.location.x
            y = event.entity.location.y
            z = event.entity.location.z
            yield = event.yield
            this.blockInfos = blockInfos
        }

        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class EntityExplodeDataReplayHandler : RecordDataReplayHandler<EntityExplodeData> {

    override fun onPlay(recordData: EntityExplodeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val explodeLocation = Location(viewer.world, recordData.x, recordData.y, recordData.z)

        // 사운드
        XSound.ENTITY_GENERIC_EXPLODE.play(viewer, 4.0F, (1.0F + (Random.nextFloat() - Random.nextFloat()) * 0.2F) * 0.7F)

        // 이팩트
        Particle.EXPLODE_HUGE.spawn(viewer, explodeLocation, 0.0F, 1)

        // 블럭 변경
        val virtualBlocks = arrayListOf<VirtualBlock>()
        recordData.blockInfos.forEach { blockInfo ->
            val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(blockInfo.x, blockInfo.y, blockInfo.z)!!
            virtualBlock.typeId = 0
            virtualBlock.typeData = 0

            virtualBlocks.add(virtualBlock)

            // 이팩트
            val explodedBlockLocation = Location(viewer.world, blockInfo.x.toDouble(), blockInfo.y.toDouble(), blockInfo.z.toDouble())

            // https://github.com/Attano/Spigot-1.8/blob/9db48bc15e203179554b8d992ca6b0a528c8d300/net/minecraft/server/v1_8_R3/Explosion.java
            val d0 = (explodedBlockLocation.x + Random.nextFloat())
            val d1 = (explodedBlockLocation.y + Random.nextFloat())
            val d2 = (explodedBlockLocation.z + Random.nextFloat())
            var d3 = d0 - blockInfo.x
            var d4 = d1 - blockInfo.y
            var d5 = d2 - blockInfo.z
            val d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5).toDouble()

            d3 /= d6
            d4 /= d6
            d5 /= d6
            var d7 = 0.5 / (d6 / 1.0 + 0.1)

            d7 *= Random.nextFloat() * Random.nextFloat() + 0.3F
            d3 *= d7
            d4 *= d7
            d5 *= d7

            Particle.EXPLODE_NORMAL.spawn(viewer, Location(viewer.world, (d0 + explodeLocation.x * 1.0) / 2.0, (d1 + explodeLocation.y * 1.0) / 2.0, (d2 + explodeLocation.z * 1.0) / 2.0), d3.toFloat(), 1)
            Particle.SMOKE.spawn(viewer, Location(viewer.world, d0, d1, d2), d3.toFloat(), 1)
        }

        replayPlayer.virtualBlockManager.showToBulk(virtualBlocks, viewer)
    }

}