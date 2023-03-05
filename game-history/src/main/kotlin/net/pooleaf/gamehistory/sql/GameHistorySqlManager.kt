package net.pooleaf.gamehistory.sql

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.plugin.CorePlugin
import net.pooleaf.gamehistory.GameHistoryPlugin
import net.pooleaf.gamehistory.sql.daos.GameDao

class GameHistorySqlManager : AbstractSqlManager(GameHistoryPlugin.instance as CorePlugin) {

    val gameDao = GameDao(this)

}