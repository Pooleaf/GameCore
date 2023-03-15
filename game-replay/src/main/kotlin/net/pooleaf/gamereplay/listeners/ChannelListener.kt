package net.pooleaf.gamereplay.listeners

import net.pooleaf.core.modules.channel.common.events.ChannelMessageEvent
import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.gamereplay.channel.ReplayChannelTask

class ChannelListener : CommonEventListener {

    @CommonEventHandler
    fun onMessage(event: ChannelMessageEvent) {
        when (event.task) {
            ReplayChannelTask.PLAY_REPLAY.name -> {
                // TODO 리플레이 재생
            }
        }
    }

}