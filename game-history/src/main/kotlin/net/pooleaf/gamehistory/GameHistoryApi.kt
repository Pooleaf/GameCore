package net.pooleaf.gamehistory

import net.pooleaf.gamehistory.configs.HistoryConfig
import net.pooleaf.gamehistory.sql.GameHistorySqlManager
import java.io.File

object GameHistoryApi {

    object unsafe {
        lateinit var sqlManager: GameHistorySqlManager


        val historyConfig: HistoryConfig by lazy {
            HistoryConfig(File(GameHistoryPlugin.instance.dataFolder, "history-config.yml"))
        }


        fun init() {
            sqlManager = GameHistorySqlManager()

            loadConfig()
        }

        fun loadConfig() {
            historyConfig.load()
            historyConfig.save()
        }
    }


    val historyConfig
        get() = unsafe.historyConfig


    fun init() {
        unsafe.init()
    }

    fun loadConfig() {
        unsafe.loadConfig()
    }

}