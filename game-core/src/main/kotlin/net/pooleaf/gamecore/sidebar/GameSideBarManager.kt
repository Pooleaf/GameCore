package net.pooleaf.gamecore.sidebar

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.common.manager.AbstractSyncManager
import net.pooleaf.gamecore.GameCore
import java.util.*

class GameSideBarManager: AbstractSyncManager<UUID, GameSideBar>() {

    var sideBar: GameSideBar? = null

    var sideBarJob: Job? = null


    fun isSideBarTimerRunning(): Boolean {
        return sideBarJob?.isActive == true
    }

    fun startSideBarTimer() {
        if (sideBar == null) error("sideBar cannot be null")
        if (isSideBarTimerRunning()) error("SideBar timer is already started")

        sideBarJob = BukkitAsyncScope.launch {
            while (GameCore.game.isRunning) {
                sideBar?.update()
                delay(1000L)
            }
        }
    }

    fun stopSideBarTimer() {
        if (!isSideBarTimerRunning()) error("SideBar timer is already not started")

        sideBarJob?.cancel()
        sideBarJob = null
    }

}