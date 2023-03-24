package net.pooleaf.gamereplay.channel

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.channel.common.channel.Channel
import net.pooleaf.core.modules.channel.common.channelgroup.ChannelGroup
import org.bukkit.entity.Player
import java.util.UUID

class ChannelManager {

    val REPLAY_CHANNEL_GROUP: String = "replay"


    fun getReplayChannelGroup(): ChannelGroup? {
        return ChannelModule.getChannelGroup(REPLAY_CHANNEL_GROUP)
    }

    fun isReplayChannelGroup(): Boolean {
        return ChannelModule.getCurrentChannel().groupName == REPLAY_CHANNEL_GROUP
    }

    /**
     * 플레이어를 리플레이 채널로 전송합니다.
     * [gameId]가 null이 아닐 경우 전송 후 해당 게임의 리플레이를 [tick] 시점부터 재생시킵니다.
     */
    fun sendToReplayChannel(
        player: Player,
        gameId: UUID? = null,
        tick: Long = 0
    ): Channel? {
        val channel = getReplayChannelGroup()?.fastJoin(player.uniqueId)

        if (channel != null && gameId != null) {
            channel.sendData(ReplayChannelTask.PLAY_REPLAY.name, player.uniqueId.toString(), gameId.toString(), tick)
        }

        return channel
    }

}