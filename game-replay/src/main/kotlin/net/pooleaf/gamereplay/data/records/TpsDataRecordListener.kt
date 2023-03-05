package net.pooleaf.gamereplay.data.records

import net.minecraft.server.v1_8_R3.MinecraftServer
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.TpsData
import net.pooleaf.gamereplay.events.RecordTickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TpsDataRecordListener : Listener {

    @EventHandler
    fun onRecordTick(event: RecordTickEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        // 10초마다 기록
        if ((event.record.currentTick.toInt() % (10 * 20)) == 0) {
            val recordData = TpsData().apply {
                tps = MinecraftServer.getServer().tps1.average
            }
            GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
        }
    }

}