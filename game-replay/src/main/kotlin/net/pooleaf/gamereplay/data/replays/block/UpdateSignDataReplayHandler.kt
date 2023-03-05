package net.pooleaf.gamereplay.data.replays.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedChatComponent
import net.pooleaf.gamereplay.data.datas.block.UpdateSignData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

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