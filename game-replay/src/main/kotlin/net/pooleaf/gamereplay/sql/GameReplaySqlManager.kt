package net.pooleaf.gamereplay.sql

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.gamereplay.GameReplayPlugin
import net.pooleaf.gamereplay.sql.daos.ReplayDao

class GameReplaySqlManager : AbstractSqlManager(GameReplayPlugin.instance) {

    val replayDao = ReplayDao(this)

}