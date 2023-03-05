package net.pooleaf.gamereplay

import net.pooleaf.core.modules.commonevent.common.CommonEventHandler
import net.pooleaf.core.modules.commonevent.common.CommonEventListener
import net.pooleaf.gamecore.events.GameCoreInitializedEvent

class GameReplayGameCoreBootstrap : CommonEventListener {

    @CommonEventHandler
    fun onGameCoreInitialized(event: GameCoreInitializedEvent) {
        GameReplayPlugin.instance.init()
    }

}