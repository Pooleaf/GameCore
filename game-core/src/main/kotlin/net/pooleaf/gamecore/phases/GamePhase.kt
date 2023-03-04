package net.pooleaf.gamecore.phases

import kotlinx.coroutines.delay
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase

open class GamePhase: Phase() {

    override suspend fun onRun() {
        while (!GameCore.game.isEnded) {
            delay(100L)
        }
    }

}