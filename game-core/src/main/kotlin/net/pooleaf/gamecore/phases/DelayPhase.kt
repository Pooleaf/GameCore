package net.pooleaf.gamecore.phases

import kotlinx.coroutines.delay
import net.pooleaf.gamecore.phase.Phase

/**
 * [delaySeconds] 만큼 기다리는 Phase
 */
class DelayPhase(
    val delaySeconds: Int,
): Phase() {

    override suspend fun onRun() {
        delay(delaySeconds * 1000L)
    }

}