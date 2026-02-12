package ai.koog.serialization.jackson

import ai.koog.serialization.JSONElement
import ai.koog.serialization.KoogSerializer
import ai.koog.serialization.TypeToken

public class JacksonSerializer : KoogSerializer {
    override fun <T> serialize(value: T, typeToken: TypeToken): String {
        TODO("Not yet implemented")
    }

    override fun <T> deserialize(value: String, typeToken: TypeToken): T {
        TODO("Not yet implemented")
    }

    override fun <T> serializeToJSONElement(
        value: T,
        typeToken: TypeToken
    ): JSONElement {
        TODO("Not yet implemented")
    }

    override fun <T> deserializeFromJSONElement(
        value: JSONElement,
        typeToken: TypeToken
    ): T {
        TODO("Not yet implemented")
    }
}
