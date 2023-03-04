package net.pooleaf.gamereplay.replay

import net.pooleaf.gamereplay.data.RecordData
import java.time.LocalDateTime
import java.util.*

data class Replay(
    val gameId: UUID,
    var recordedPlayers: List<UUID>
) {

    var createdAt: LocalDateTime? = null
    var endTick: Long = 0L

    var recordDatas: HashMap<Long, LinkedList<RecordData>> = hashMapOf()

}