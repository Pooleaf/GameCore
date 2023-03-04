package net.pooleaf.gamereplay.replay.virtual.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.ChunkCoordIntPair
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo
import com.comphenix.protocol.wrappers.WrappedBlockData
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.ItemUtil
import net.pooleaf.core.modules.support.common.manager.AbstractManager
import net.pooleaf.gamereplay.replay.virtual.VirtualLocation
import org.bukkit.entity.Player

/**
 * 가상 블럭 관리자
 */
class VirtualBlockManager : AbstractManager<VirtualLocation, VirtualBlock>() {

    fun getByXyz(x: Int, y: Int, z: Int): VirtualBlock? {
        return datas.filterKeys { it.x.toInt() == x && it.y.toInt() == y && it.z.toInt() == z }
            .map { it.value }
            .firstOrNull()
    }

    fun getByChunk(chunkX: Int, chunkZ: Int): List<VirtualBlock> {
        return datas.filterKeys { chunkX == it.getChunk().chunkX && chunkZ == it.getChunk().chunkZ }
            .map { it.value }
            .toList()
    }

    fun showToBulk(virtualBlocks: List<VirtualBlock>, viewer: Player) {
        var chunkBlocks = hashMapOf<ChunkCoordIntPair, ArrayList<MultiBlockChangeInfo>>()

        virtualBlocks.forEach { virtualBlock ->
            val chunk = virtualBlock.location.getChunk()
            val bukkitLocation = virtualBlock.location.toBukkitLocation(viewer.world)

            var blocks = chunkBlocks.get(chunk)
            if (blocks == null) {
                blocks = arrayListOf()
                chunkBlocks.put(chunk, blocks)
            }

            var typeId = virtualBlock.typeId
            var typeData = virtualBlock.typeData

            // 데이터가 없을 경우 실제 블럭 데이터를 보냄
            if (virtualBlock.typeId == -1) {
                BukkitSyncScope.launch {
                    val block = bukkitLocation.world.getBlockAt(bukkitLocation)
                    typeId = block.typeId
                    typeData = block.data

                    val wrappedBlockData = WrappedBlockData.createData(ItemUtil.getMaterial(typeId), typeData.toInt())
                    val multiBlockChangeInfo = MultiBlockChangeInfo(bukkitLocation, wrappedBlockData)

                    blocks.add(multiBlockChangeInfo)
                }
            } else {
                val wrappedBlockData = WrappedBlockData.createData(ItemUtil.getMaterial(typeId), typeData.toInt())
                val multiBlockChangeInfo = MultiBlockChangeInfo(bukkitLocation, wrappedBlockData)

                blocks.add(multiBlockChangeInfo)
            }
        }

        chunkBlocks.forEach { chunk, multiBlockChangeInfos ->
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE)
            packet.chunkCoordIntPairs.write(0, chunk)
            packet.multiBlockChangeInfoArrays.write(0, multiBlockChangeInfos.toTypedArray())
            ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
        }
    }

}