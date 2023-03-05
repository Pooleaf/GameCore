package net.pooleaf.gamehistory

import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.gamecore.events.GameCoreInitializedEvent

class GameHistoryGameCoreBootstrap : CommonEventListener {

    @CommonEventHandler
    fun onGameCoreInitialized(event: GameCoreInitializedEvent) {
        GameHistoryPlugin.instance.init()
    }

}