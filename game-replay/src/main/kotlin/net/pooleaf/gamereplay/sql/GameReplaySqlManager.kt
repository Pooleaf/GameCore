package net.pooleaf.gamereplay.sql

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.plugin.CorePlugin
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamereplay.sql.daos.ReplayDao

class GameReplaySqlManager : AbstractSqlManager(GameCore.gamePlugin as CorePlugin) {

    val replayDao = ReplayDao(this)

}