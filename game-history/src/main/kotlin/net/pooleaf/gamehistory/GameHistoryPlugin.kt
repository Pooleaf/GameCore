package net.pooleaf.gamehistory

import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.plugin.BukkitCorePlugin
import org.bukkit.Bukkit

class GameHistoryPlugin : BukkitCorePlugin() {

    companion object {
        lateinit var instance: GameHistoryPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ GameHistory ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        if (Bukkit.getPluginManager().getPlugin("GameCore") == null) {
            init()
        } else {
            CommonEventModule.registerListener(this, GameHistoryGameCoreBootstrap())
        }
    }

    internal fun init() {
        GameHistoryApi.init()

        GameHistoryApi.unsafe.sqlManager.connect()
        loadConfig()

        registerEventListeners()

        Logger.log("플러그인이 초기화되었습니다.")
    }

    override fun onEnd() {
        GameHistoryApi.unsafe.sqlManager.close()
    }

    override fun onConfigLoaded() {
        GameHistoryApi.loadConfig()
    }

}