package net.pooleaf.gamereplay.data.datas.game

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 팀 탈락 데이터
 */
data class TeamDefeatData(
    var teamId: Int = -1,
    var teamName: String? = null,
    var teamPlayerUuids: List<UUID> = arrayListOf(),
    var killerPlayerUuid: UUID? = null
) : RecordData {

    override val type: String = "teamDefeat"

}