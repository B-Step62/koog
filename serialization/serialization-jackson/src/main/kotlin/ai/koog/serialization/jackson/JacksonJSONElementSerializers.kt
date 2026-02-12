package ai.koog.serialization.jackson

import ai.koog.serialization.JSONArray
import ai.koog.serialization.JSONElement
import ai.koog.serialization.JSONLiteral
import ai.koog.serialization.JSONNull
import ai.koog.serialization.JSONObject
import ai.koog.serialization.JSONPrimitive
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.addDeserializer
import com.fasterxml.jackson.module.kotlin.addSerializer

/**
 * Jackson module that registers custom serializers and deserializers for [JSONElement] types.
 */
public class JSONElementModule : SimpleModule() {
    init {
        addSerializer(JSONElement::class, JSONElementSerializer())
        addSerializer(JSONObject::class, JSONObjectSerializer())
        addSerializer(JSONArray::class, JSONArraySerializer())
        addSerializer(JSONPrimitive::class, JSONPrimitiveSerializer())
        addSerializer(JSONLiteral::class, JSONLiteralSerializer())
        addSerializer(JSONNull::class, JSONNullSerializer())

        addDeserializer(JSONElement::class, JSONElementDeserializer())
        addDeserializer(JSONObject::class, JSONObjectDeserializer())
        addDeserializer(JSONArray::class, JSONArrayDeserializer())
        addDeserializer(JSONPrimitive::class, JSONPrimitiveDeserializer())
        addDeserializer(JSONLiteral::class, JSONLiteralDeserializer())
    }
}

// Serializers

private class JSONElementSerializer : JsonSerializer<JSONElement>() {
    override fun serialize(value: JSONElement, gen: JsonGenerator, serializers: SerializerProvider) {
        val jsonNode = value.toJacksonJsonNode()
        gen.writeTree(jsonNode)
    }
}

private class JSONObjectSerializer : JsonSerializer<JSONObject>() {
    override fun serialize(value: JSONObject, gen: JsonGenerator, serializers: SerializerProvider) {
        val jsonNode = value.toJacksonObjectNode()
        gen.writeTree(jsonNode)
    }
}

private class JSONArraySerializer : JsonSerializer<JSONArray>() {
    override fun serialize(value: JSONArray, gen: JsonGenerator, serializers: SerializerProvider) {
        val jsonNode = value.toJacksonArrayNode()
        gen.writeTree(jsonNode)
    }
}

private class JSONPrimitiveSerializer : JsonSerializer<JSONPrimitive>() {
    override fun serialize(value: JSONPrimitive, gen: JsonGenerator, serializers: SerializerProvider) {
        val jsonNode = value.toJacksonJsonNode()
        gen.writeTree(jsonNode)
    }
}

private class JSONLiteralSerializer : JsonSerializer<JSONLiteral>() {
    override fun serialize(value: JSONLiteral, gen: JsonGenerator, serializers: SerializerProvider) {
        val jsonNode = value.toJacksonJsonNode()
        gen.writeTree(jsonNode)
    }
}

private class JSONNullSerializer : JsonSerializer<JSONNull>() {
    override fun serialize(value: JSONNull, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeNull()
    }
}

// Deserializers

private class JSONElementDeserializer : JsonDeserializer<JSONElement>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JSONElement {
        val jsonNode = p.readValueAsTree<JsonNode>()
        return if (jsonNode == null || jsonNode.isNull) JSONNull else jsonNode.toJSONElement()
    }
}

private class JSONObjectDeserializer : JsonDeserializer<JSONObject>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JSONObject {
        val jsonNode = p.readValueAsTree<JsonNode>()
        return jsonNode.toJSONObject()
    }
}

private class JSONArrayDeserializer : JsonDeserializer<JSONArray>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JSONArray {
        val jsonNode = p.readValueAsTree<JsonNode>()
        return jsonNode.toJSONArray()
    }
}

private class JSONPrimitiveDeserializer : JsonDeserializer<JSONPrimitive>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JSONPrimitive {
        val jsonNode = p.readValueAsTree<JsonNode>()
        return if (jsonNode == null || jsonNode.isNull) JSONNull else jsonNode.toJSONPrimitive()
    }
}

private class JSONLiteralDeserializer : JsonDeserializer<JSONLiteral>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JSONLiteral {
        val jsonNode = p.readValueAsTree<JsonNode>()
        val primitive = jsonNode.toJSONPrimitive()
        return primitive as? JSONLiteral
            ?: error("Expected JSONLiteral but got ${primitive::class.simpleName}")
    }
}
