package ai.koog.serialization.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serializer for [JSONElement] that delegates to kotlinx-serialization [JsonElement].
 */
public object JSONElementSerializer : KSerializer<JSONElement> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONElement) {
        encoder.encodeSerializableValue(JsonElement.serializer(), value.toJsonElement())
    }

    override fun deserialize(decoder: Decoder): JSONElement {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        return jsonElement.toJSONElement()
    }
}

/**
 * Serializer for [JSONObject] that delegates to kotlinx-serialization [JsonObject].
 */
public object JSONObjectSerializer : KSerializer<JSONObject> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONObject) {
        encoder.encodeSerializableValue(JsonObject.serializer(), value.toJsonObject())
    }

    override fun deserialize(decoder: Decoder): JSONObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return jsonObject.toJSONObject()
    }
}

/**
 * Serializer for [JSONArray] that delegates to kotlinx-serialization [JsonArray].
 */
public object JSONArraySerializer : KSerializer<JSONArray> {
    override val descriptor: SerialDescriptor = JsonArray.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONArray) {
        encoder.encodeSerializableValue(JsonArray.serializer(), value.toJsonArray())
    }

    override fun deserialize(decoder: Decoder): JSONArray {
        val jsonArray = decoder.decodeSerializableValue(JsonArray.serializer())
        return jsonArray.toJSONArray()
    }
}

/**
 * Serializer for [JSONPrimitive] that delegates to kotlinx-serialization [JsonPrimitive].
 */
public object JSONPrimitiveSerializer : KSerializer<JSONPrimitive> {
    override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONPrimitive) {
        encoder.encodeSerializableValue(JsonPrimitive.serializer(), value.toJsonPrimitive())
    }

    override fun deserialize(decoder: Decoder): JSONPrimitive {
        val jsonPrimitive = decoder.decodeSerializableValue(JsonPrimitive.serializer())
        return jsonPrimitive.toJSONPrimitive()
    }
}

/**
 * Serializer for [JSONLiteral] that delegates to kotlinx-serialization [JsonPrimitive].
 */
public object JSONLiteralSerializer : KSerializer<JSONLiteral> {
    override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONLiteral) {
        encoder.encodeSerializableValue(JsonPrimitive.serializer(), value.toJsonPrimitive())
    }

    override fun deserialize(decoder: Decoder): JSONLiteral {
        val jsonPrimitive = decoder.decodeSerializableValue(JsonPrimitive.serializer())
        return when (val primitive = jsonPrimitive.toJSONPrimitive()) {
            is JSONLiteral -> primitive
            is JSONNull -> throw IllegalStateException("Expected JSONLiteral but got JSONNull")
        }
    }
}

/**
 * Serializer for [JSONNull] that delegates to kotlinx-serialization [JsonNull].
 */
public object JSONNullSerializer : KSerializer<JSONNull> {
    override val descriptor: SerialDescriptor = JsonNull.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONNull) {
        encoder.encodeSerializableValue(JsonNull.serializer(), JsonNull)
    }

    override fun deserialize(decoder: Decoder): JSONNull {
        decoder.decodeSerializableValue(JsonNull.serializer())
        return JSONNull
    }
}
