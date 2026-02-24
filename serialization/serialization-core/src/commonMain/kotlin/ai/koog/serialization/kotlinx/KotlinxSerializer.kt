package ai.koog.serialization.kotlinx

import ai.koog.serialization.JSONElement
import ai.koog.serialization.KoogSerializer
import ai.koog.serialization.TypeToken
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Serializer that uses kotlinx-serialization
 *
 * @property json Kotlinx Json instance to use for serialization/deserialization
 */
public class KotlinxSerializer(
    public val json: Json = Json.Default,
) : KoogSerializer {
    override fun <T> serializeToString(value: T, typeToken: TypeToken): String {
        return json.encodeToString(kotlinxSerializer(typeToken), value)
    }

    override fun <T> deserializeFromString(value: String, typeToken: TypeToken): T {
        return json.decodeFromString(kotlinxSerializer(typeToken), value)
    }

    override fun <T> serializeToJSONElement(value: T, typeToken: TypeToken): JSONElement {
        return json.encodeToJsonElement(kotlinxSerializer(typeToken), value).toJSONElement()
    }

    override fun <T> deserializeFromJSONElement(value: JSONElement, typeToken: TypeToken): T {
        return json.decodeFromJsonElement(kotlinxSerializer(typeToken), value.toKotlinxJsonElement())
    }
}

internal expect fun <T> kotlinxSerializer(typeToken: TypeToken): KSerializer<T>
