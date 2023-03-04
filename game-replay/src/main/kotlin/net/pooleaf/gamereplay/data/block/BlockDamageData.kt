package net.pooleaf.gamereplay.data.block

import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.events.RecordTickEvent
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

data class BlockDamageData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var state: Int = 0
) : RecordData {

    override val type: String = "blockDamage"

}

class BlockDamageDataRecordListener : Listener {

    @EventHandler
    fun onRecordTick(event: RecordTickEvent) {
        event.record.recordTargetPlayers.forEach { uuid ->
            val player = Bukkit.getPlayer(uuid)
            if (player == null) return@forEach

            val entityPlayer = BukkitReflectionUtil.getHandle(player) as EntityPlayer
            val playerInteractManager = entityPlayer.playerInteractManager

            // isDestroyingBlock
            val dField = playerInteractManager::class.java.getDeclaredField("d")
            dField.isAccessible = true
            val isDestroyingBlock = dField.getBoolean(playerInteractManager)

            // destroyPos
            val fField = playerInteractManager::class.java.getDeclaredField("f")
            fField.isAccessible = true
            val destroyPos = fField.get(playerInteractManager) as BlockPosition

            // hasDelayedDestroy
            val hField = playerInteractManager::class.java.getDeclaredField("h")
            hField.isAccessible = true
            val hasDelayedDestroy = hField.getBoolean(playerInteractManager)

            // delayedDestroyPos
            val iField = playerInteractManager::class.java.getDeclaredField("i")
            iField.isAccessible = true
            val delayedDestroyPos = iField.get(playerInteractManager) as BlockPosition

            // lastSentState
            val kField = playerInteractManager::class.java.getDeclaredField("k")
            kField.isAccessible = true
            val lastSentState = kField.getInt(playerInteractManager)

            if (!hasDelayedDestroy && !isDestroyingBlock) return@forEach

            // 현재 블럭 상태 저장
            if (hasDelayedDestroy) {
                val recordData = BlockDamageData().apply {
                    x = delayedDestroyPos.x
                    y = delayedDestroyPos.y
                    z = delayedDestroyPos.z
                    state = lastSentState
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            } else if (isDestroyingBlock) {
                val recordData = BlockDamageData().apply {
                    x = destroyPos.x
                    y = destroyPos.y
                    z = destroyPos.z
                    state = lastSentState
                }
                GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
            }
        }
    }

}

class BlockDamageDataReplayHandler : RecordDataReplayHandler<BlockDamageData> {

    override fun onPlay(recordData: BlockDamageData, viewer: Player) {
        val blockPosition = BlockPosition(recordData.x, recordData.y, recordData.z)
        val packet = PacketPlayOutBlockBreakAnimation(viewer.entityId, blockPosition, recordData.state)
        BukkitReflectionUtil.sendPacket(viewer, packet)
    }

}