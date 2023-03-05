package net.pooleaf.gamereplay.data.replays.block

import com.cryptomorin.xseries.XSound
import net.minecraft.server.v1_8_R3.MathHelper
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import net.pooleaf.core.modules.support.bukkit.sound.play
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.ExplodeData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import net.pooleaf.gamereplay.replay.virtual.block.VirtualBlock
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.random.Random

class ExplodeDataReplayHandler : RecordDataReplayHandler<ExplodeData> {

    override fun onPlay(recordData: ExplodeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val explodeLocation = Location(viewer.world, recordData.x, recordData.y, recordData.z)

        // 사운드
        XSound.ENTITY_GENERIC_EXPLODE.play(viewer, explodeLocation, 4.0F, (1.0F + (Random.nextFloat() - Random.nextFloat()) * 0.2F) * 0.7F)

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