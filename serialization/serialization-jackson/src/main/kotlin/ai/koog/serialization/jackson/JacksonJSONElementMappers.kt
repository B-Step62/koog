package ai.koog.serialization.jackson

import ai.koog.serialization.JSONArray
import ai.koog.serialization.JSONElement
import ai.koog.serialization.JSONLiteral
import ai.koog.serialization.JSONNull
import ai.koog.serialization.JSONObject
import ai.koog.serialization.JSONPrimitive
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode

//region Jackson to Koog serialization

/**
 * Converts Jackson [JsonNode] to [ai.koog.serialization.JSONElement].
 */
public fun JsonNode.toJSONElement(): JSONElement = when {
    isObject -> toJSONObject()
    isArray -> toJSONArray()
    else -> toJSONPrimitive()
}

/**
 * Converts Jackson [ObjectNode] to [ai.koog.serialization.JSONObject].
 */
public fun JsonNode.toJSONObject(): JSONObject {
    require(isObject) { "JsonNode is not an object" }
    return JSONObject(
        entries = this.fieldNames().asSequence().associateWith { fieldName ->
            this.get(fieldName).toJSONElement()
        }
    )
}

/**
 * Converts Jackson [ArrayNode] to [ai.koog.serialization.JSONArray].
 */
public fun JsonNode.toJSONArray(): JSONArray {
    require(isArray) { "JsonNode is not an array" }
    return JSONArray(
        elements = map { it.toJSONElement() }
    )
}

/**
 * Converts Jackson primitive [JsonNode] to [ai.koog.serialization.JSONPrimitive].
 */
public fun JsonNode.toJSONPrimitive(): JSONPrimitive = when {
    isNull -> JSONNull
    isTextual -> JSONLiteral(asText(), isString = true)
    isNumber -> JSONLiteral(asText(), isString = false)
    isBoolean -> JSONLiteral(asText(), isString = false)
    else -> error("Unsupported JsonNode type: ${this::class.simpleName}")
}

//endregion

//region Koog serialization to Jackson

/**
 * Converts [JSONElement] to Jackson [JsonNode].
 */
public fun JSONElement.toJacksonJsonNode(): JsonNode = when (this) {
    is JSONObject -> toJacksonObjectNode()
    is JSONArray -> toJacksonArrayNode()
    is JSONPrimitive -> toJacksonJsonNode()
}

/**
 * Converts [JSONObject] to Jackson [ObjectNode].
 */
public fun JSONObject.toJacksonObjectNode(): ObjectNode {
    val objectNode = JsonNodeFactory.instance.objectNode()
    entries.forEach { (key, value) ->
        objectNode.set<JsonNode>(key, value.toJacksonJsonNode())
    }
    return objectNode
}

/**
 * Converts [JSONArray] to Jackson [ArrayNode].
 */
public fun JSONArray.toJacksonArrayNode(): ArrayNode {
    val arrayNode = JsonNodeFactory.instance.arrayNode()
    elements.forEach { element ->
        arrayNode.add(element.toJacksonJsonNode())
    }
    return arrayNode
}

/**
 * Converts [JSONPrimitive] to Jackson [JsonNode].
 */
public fun JSONPrimitive.toJacksonJsonNode(): JsonNode = when (this) {
    is JSONNull -> NullNode.instance

    is JSONLiteral -> if (isString) {
        JsonNodeFactory.instance.textNode(content)
    } else {
        // For unquoted literals (numbers, booleans), parse the content
        when {
            content == "true" -> JsonNodeFactory.instance.booleanNode(true)

            content == "false" -> JsonNodeFactory.instance.booleanNode(false)

            content.contains('.') -> JsonNodeFactory.instance.numberNode(content.toDouble())

            else -> {
                // Try to parse as Long first, then fallback to text node for very large numbers
                val longValue = content.toLongOrNull()
                if (longValue != null) {
                    JsonNodeFactory.instance.numberNode(longValue)
                } else {
                    // For numbers too large for Long, use the POJONode or textNode to preserve precision
                    JsonNodeFactory.instance.numberNode(content.toBigInteger())
                }
            }
        }
    }
}

//endregion
