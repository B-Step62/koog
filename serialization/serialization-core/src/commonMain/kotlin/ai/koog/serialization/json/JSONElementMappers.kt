package ai.koog.serialization.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

//region kotlinx-serialization to Koog serialization

/**
 * Converts kotlinx-serialization [JsonElement] to [JSONElement].
 */
public fun JsonElement.toJSONElement(): JSONElement = when (this) {
    is JsonObject -> toJSONObject()
    is JsonArray -> toJSONArray()
    is JsonPrimitive -> toJSONPrimitive()
}

/**
 * Converts kotlinx-serialization [JsonObject] to [JSONObject].
 */
public fun JsonObject.toJSONObject(): JSONObject = JSONObject(
    entries = mapValues { (_, value) -> value.toJSONElement() }
)

/**
 * Converts kotlinx-serialization [JsonArray] to [JSONArray].
 */
public fun JsonArray.toJSONArray(): JSONArray = JSONArray(
    elements = map { it.toJSONElement() }
)

/**
 * Converts kotlinx-serialization [JsonPrimitive] to [JSONPrimitive].
 */
public fun JsonPrimitive.toJSONPrimitive(): JSONPrimitive = when (this) {
    is JsonNull -> JSONNull
    else -> JSONLiteral(content = this.content, isString = this.isString)
}

//endregion

//region Koog serialization to kotlinx-serialization

/**
 * Converts [JSONElement] to kotlinx-serialization [JsonElement].
 */
public fun JSONElement.toJsonElement(): JsonElement = when (this) {
    is JSONObject -> toJsonObject()
    is JSONArray -> toJsonArray()
    is JSONPrimitive -> toJsonPrimitive()
}

/**
 * Converts [JSONObject] to kotlinx-serialization [JsonObject].
 */
public fun JSONObject.toJsonObject(): JsonObject = buildJsonObject {
    entries.forEach { (key, value) ->
        put(key, value.toJsonElement())
    }
}

/**
 * Converts [JSONArray] to kotlinx-serialization [JsonArray].
 */
public fun JSONArray.toJsonArray(): JsonArray = buildJsonArray {
    elements.forEach { element ->
        add(element.toJsonElement())
    }
}

/**
 * Converts [JSONPrimitive] to kotlinx-serialization [JsonPrimitive].
 */
public fun JSONPrimitive.toJsonPrimitive(): JsonPrimitive = when (this) {
    is JSONNull -> JsonNull

    is JSONLiteral -> if (isString) {
        JsonPrimitive(content)
    } else {
        JsonUnquotedLiteral(content)
    }
}

//endregion
