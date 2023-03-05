package net.pooleaf.gamereplay.data.replays

import net.pooleaf.gamereplay.data.datas.TpsData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class TpsDataReplayHandler : RecordDataReplayHandler<TpsData> {

    override fun onPlay(recordData: TpsData, viewer: Player) {
        // TODO
    }

}