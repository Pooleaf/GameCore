package net.pooleaf.gamereplay.replay.virtual

import net.pooleaf.gamereplay.data.RecordData

open abstract class VirtualHistory {

    var histories: HashMap<Long, ArrayList<RecordData>> = hashMapOf()


    protected fun getLastData(clazz: Class<out RecordData>, tick: Long): RecordData? {
        return histories.filterKeys { it <= tick }
            .filterValues { it.filterIsInstance(clazz).isNotEmpty() }
            .maxByOrNull { it.key }
            ?.value
            ?.filterIsInstance(clazz)
            ?.firstOrNull()
    }

    protected fun getLastDataTick(clazz: Class<out RecordData>, tick: Long): Long? {
        return histories.filterKeys { it <= tick }
            .filterValues { it.filterIsInstance(clazz).isNotEmpty() }
            .maxByOrNull { it.key }
            ?.key
    }

    protected fun getCurrentData(clazz: Class<out RecordData>, tick: Long): List<RecordData>? {
        return histories.get(tick)
            ?.filterIsInstance(clazz)
    }

}