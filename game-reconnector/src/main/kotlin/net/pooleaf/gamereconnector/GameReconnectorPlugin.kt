package net.pooleaf.gamereconnector

import net.pooleaf.core.plugin.BukkitCorePlugin

class GameReconnectorPlugin : BukkitCorePlugin() {

    override fun onStart() {
        registerEventListeners()
        registerCommands()
    }

}