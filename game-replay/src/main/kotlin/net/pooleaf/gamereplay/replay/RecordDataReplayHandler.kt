package net.pooleaf.gamereplay.replay

import net.pooleaf.gamereplay.data.RecordData
import org.bukkit.entity.Player

interface RecordDataReplayHandler<out T: RecordData> {

    /**
     * 해당 녹화 데이터 재생 시 호출됩니다.
     */
    fun onPlay(recordData: @UnsafeVariance T, viewer: Player)

}