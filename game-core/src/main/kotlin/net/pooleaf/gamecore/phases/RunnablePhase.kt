package net.pooleaf.gamecore.phases

import net.pooleaf.gamecore.phase.Phase

/**
 * [delaySeconds] 만큼 기다리는 Phase
 */
class RunnablePhase(
    val runnable: Runnable
): Phase() {

    override suspend fun onRun() {
        runnable.run()
    }

}