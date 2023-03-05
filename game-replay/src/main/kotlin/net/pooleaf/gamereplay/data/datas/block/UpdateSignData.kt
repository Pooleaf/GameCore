package net.pooleaf.gamereplay.data.datas.block

import net.pooleaf.gamereplay.data.RecordData

/**
 * 표지판 업데이트 데이터
 * 관리자에게만 전송
 */
data class UpdateSignData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var lines: Array<String> = arrayOf()
) : RecordData {

    override val type: String = "updateSign"


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateSignData

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (!lines.contentEquals(other.lines)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + lines.contentHashCode()
        return result
    }

}