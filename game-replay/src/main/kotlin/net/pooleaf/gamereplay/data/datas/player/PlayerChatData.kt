package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 채팅 데이터
 * 관리자에게만 전송
 */
data class PlayerChatData(
    var playerUuid: UUID? = null,
    var message: String? = null
) : RecordData {

    override val type: String = "playerChat"

}