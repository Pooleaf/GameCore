package net.pooleaf.gamehistory

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.gamecore.events.GameCoreInitializedEvent

class GameHistoryGameCoreBootstrap : CommonEventListener {

    @CommonEventHandler
    fun onGameCoreInitialized(event: GameCoreInitializedEvent) {
        GameHistoryPlugin.instance.init()

        val currentChannel = ChannelModule.getCurrentChannel()
        GameHistoryApi.unsafe.sqlManager.gameDao.updateNotEndedGame(currentChannel.name)
    }

}