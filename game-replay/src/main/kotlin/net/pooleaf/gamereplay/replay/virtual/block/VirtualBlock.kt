package net.pooleaf.gamereplay.replay.virtual.block

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.block.BlockDamageData
import net.pooleaf.gamereplay.replay.virtual.VirtualLocation
import org.bukkit.entity.Player

/**
 * 가상 블럭 정보
 * 만약 데이터가 없을 경우 -1
 */
data class VirtualBlock(
    val location: VirtualLocation,
    var typeId: Int = -1,
    var typeData: Byte = 0,
    var damageState: Int = 0
) {

    var histories: HashMap<Long, VirtualBlock> = hashMapOf()


    /**
     * 현재 정보 전송
     */
    fun showTo(viewer: Player) {
        val bukkitLocation = location.toBukkitLocation(viewer.world)

        // 데이터가 없을 경우 실제 블럭 데이터를 보냄
        if (typeId == -1) {
            val block = bukkitLocation.world.getBlockAt(location.x.toInt(), location.y.toInt(), location.z.toInt())
            viewer.sendBlockChange(bukkitLocation, block.typeId, block.data)
        }
        // 데이터가 있을 경우 가상 블럭 데이터를 보냄
        else {
            viewer.sendBlockChange(bukkitLocation, typeId, typeData)

            if (damageState != 0) {
                val blockDamageHandler = GameReplayApi.unsafe.recordDataManager.get(BlockDamageData::class.java) ?: return

                val blockDamageData = BlockDamageData()
                blockDamageData.x = location.x.toInt()
                blockDamageData.y = location.y.toInt()
                blockDamageData.z = location.z.toInt()
                blockDamageData.state = damageState

                blockDamageHandler.onPlay(blockDamageData, viewer)
            }
        }
    }

    fun timeMachine(tick: Long) {
        val data = histories.filterKeys { it <= tick }
            .maxByOrNull { it.key }
            ?.value

        if (data == null) {
            typeId = -1
            typeData = 0
            damageState = 0
        } else {
            typeId = data.typeId
            typeData = data.typeData
            damageState = data.damageState
        }
    }

}