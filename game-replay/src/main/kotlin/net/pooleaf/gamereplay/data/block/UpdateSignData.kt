package net.pooleaf.gamereplay.data.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedChatComponent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

/**
 * 표지판 업데이트 데이터
 * 관리자에게만 전송
 */
data class UpdateSignData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var lines: Array<String> = arrayOf()
) : RecordData {

    override val type: String = "updateSign"


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateSignData

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (!lines.contentEquals(other.lines)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + lines.contentHashCode()
        return result
    }

}

class UpdateSignDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSignChange(event: SignChangeEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val block = event.block
        val location = block.location

        val recordData = UpdateSignData().apply {
            x = location.x.toInt()
            y = location.y.toInt()
            z = location.z.toInt()
            lines = event.lines
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class UpdateSignDataReplayHandler : RecordDataReplayHandler<UpdateSignData> {

    override fun onPlay(recordData: UpdateSignData, viewer: Player) {
        if (!viewer.isOp) return

        val wrappedChatComponents = recordData.lines.map { WrappedChatComponent.fromLegacyText(it) }.toTypedArray()

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_SIGN)
        packet.blockPositionModifier.write(0, BlockPosition(recordData.x, recordData.y, recordData.z))
        packet.chatComponentArrays.write(0, wrappedChatComponents)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}