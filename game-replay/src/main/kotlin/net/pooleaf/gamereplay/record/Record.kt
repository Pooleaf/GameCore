package net.pooleaf.gamereplay.record

import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.Replay
import java.util.*

class Record(
    uuid: UUID,
    val recordTargetPlayers: List<UUID>
) {

    var isRecording: Boolean = false

    var currentTick: Float = 0.0F

    val replay: Replay = Replay(uuid, recordTargetPlayers)


    /**
     * 현재 틱에 녹화 데이터를 추가합니다.
     */
    fun addRecordData(recordData: RecordData) {
        // 현재 틱 녹화 데이터 리스트 불러오기
        var tickRecordDatas = replay.recordDatas.get(currentTick.toLong())

        if (tickRecordDatas == null) {
            tickRecordDatas = LinkedList<RecordData>()
            replay.recordDatas.put(currentTick.toLong(), tickRecordDatas)
        }

        // 중복 체크 (패킷을 여러 플레이어에게 보내면 데이터가 중복됨)
        if (tickRecordDatas.contains(recordData)) return

        // 현재 틱에 녹화 데이터 추가
        tickRecordDatas.add(recordData)
    }

    /**
     * 틱에 해당하는 녹화 데이터를 반환합니다.
     */
    fun getRecordData(tick: Long): LinkedList<RecordData>? {
        return replay.recordDatas.get(tick)
    }

}