package ai.koog.serialization.kotlinx

import ai.koog.serialization.JSONArray
import ai.koog.serialization.JSONElement
import ai.koog.serialization.JSONLiteral
import ai.koog.serialization.JSONNull
import ai.koog.serialization.JSONObject
import ai.koog.serialization.JSONPrimitive
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
 * Serializer for [ai.koog.serialization.JSONElement] that delegates to kotlinx-serialization [JsonElement].
 */
public object KxJSONElementSerializer : KSerializer<JSONElement> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONElement) {
        encoder.encodeSerializableValue(JsonElement.serializer(), value.toKxJsonElement())
    }

    override fun deserialize(decoder: Decoder): JSONElement {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        return jsonElement.toJSONElement()
    }
}

/**
 * Serializer for [ai.koog.serialization.JSONObject] that delegates to kotlinx-serialization [JsonObject].
 */
public object KxJSONObjectSerializer : KSerializer<JSONObject> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONObject) {
        encoder.encodeSerializableValue(JsonObject.serializer(), value.toKxJsonObject())
    }

    override fun deserialize(decoder: Decoder): JSONObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return jsonObject.toJSONObject()
    }
}

/**
 * Serializer for [ai.koog.serialization.JSONArray] that delegates to kotlinx-serialization [JsonArray].
 */
public object KxJSONArraySerializer : KSerializer<JSONArray> {
    override val descriptor: SerialDescriptor = JsonArray.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONArray) {
        encoder.encodeSerializableValue(JsonArray.serializer(), value.toKxJsonArray())
    }

    override fun deserialize(decoder: Decoder): JSONArray {
        val jsonArray = decoder.decodeSerializableValue(JsonArray.serializer())
        return jsonArray.toJSONArray()
    }
}

/**
 * Serializer for [ai.koog.serialization.JSONPrimitive] that delegates to kotlinx-serialization [JsonPrimitive].
 */
public object KxJSONPrimitiveSerializer : KSerializer<JSONPrimitive> {
    override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONPrimitive) {
        encoder.encodeSerializableValue(JsonPrimitive.serializer(), value.toKxJsonPrimitive())
    }

    override fun deserialize(decoder: Decoder): JSONPrimitive {
        val jsonPrimitive = decoder.decodeSerializableValue(JsonPrimitive.serializer())
        return jsonPrimitive.toJSONPrimitive()
    }
}

/**
 * Serializer for [ai.koog.serialization.JSONLiteral] that delegates to kotlinx-serialization [JsonPrimitive].
 */
public object KxJSONLiteralSerializer : KSerializer<JSONLiteral> {
    override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONLiteral) {
        encoder.encodeSerializableValue(JsonPrimitive.serializer(), value.toKxJsonPrimitive())
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
public object KxJSONNullSerializer : KSerializer<JSONNull> {
    override val descriptor: SerialDescriptor = JsonNull.serializer().descriptor

    override fun serialize(encoder: Encoder, value: JSONNull) {
        encoder.encodeSerializableValue(JsonNull.serializer(), JsonNull)
    }

    override fun deserialize(decoder: Decoder): JSONNull {
        decoder.decodeSerializableValue(JsonNull.serializer())
        return JSONNull
    }
}
