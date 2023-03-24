package net.pooleaf.gamereplay.replay

import net.pooleaf.core.lib.com.google.gson.JsonDeserializationContext
import net.pooleaf.core.lib.com.google.gson.JsonDeserializer
import net.pooleaf.core.lib.com.google.gson.JsonElement
import net.pooleaf.core.modules.support.common.util.GsonUtil
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import java.lang.reflect.Type

class RecordDataDeserializer : JsonDeserializer<RecordData> {

    override fun deserialize(
        jsonElement: JsonElement,
        type: Type?,
        jsonDeserializationContext: JsonDeserializationContext
    ): RecordData? {
        val jsonObject = jsonElement.asJsonObject
        if (!jsonObject.has("type")) return null

        val type = jsonObject.get("type").asString
        val recordDataClass = GameReplayApi.unsafe.recordDataManager.getRecordDataClassByType(type)
            ?: error("Cannot deserialize RecordData type ${type}")

        val recordData = recordDataClass.newInstance()
        GsonUtil.loadFromJson(jsonObject.toString(), recordData)

        return recordData
    }

}