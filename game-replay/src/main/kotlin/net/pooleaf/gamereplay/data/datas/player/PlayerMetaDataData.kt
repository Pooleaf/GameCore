package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * Entity Index 0만 녹화함
 * https://wiki.vg/index.php?title=Entity_metadata&oldid=7415#Entity
 */
class PlayerMetaDataData(
    var playerUuid: UUID? = null,
    var index: Int = 0,
    var value: Byte = 0
) : RecordData {

    override val type: String = "playerMetaData"

}