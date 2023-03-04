package net.pooleaf.gamehistory.sql

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.plugin.CorePlugin
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamehistory.sql.daos.GameDao

class GameHistorySqlManager : AbstractSqlManager(GameCore.gamePlugin as CorePlugin) {

    val gameDao = GameDao(this)

}