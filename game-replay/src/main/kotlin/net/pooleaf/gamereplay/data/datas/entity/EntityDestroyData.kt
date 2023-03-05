package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

data class EntityDestroyData(
    var entityIds: Array<Int> = arrayOf()
) : RecordData {

    override val type: String = "entityDestroy"


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityDestroyData

        if (!entityIds.contentEquals(other.entityIds)) return false

        return true
    }

    override fun hashCode(): Int {
        return entityIds.contentHashCode()
    }

}