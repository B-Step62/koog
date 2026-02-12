package ai.koog.serialization.jackson

import ai.koog.serialization.JSONElement
import ai.koog.serialization.JSONNull
import ai.koog.serialization.JSONPrimitive
import ai.koog.serialization.JavaTypeToken
import ai.koog.serialization.KoogSerializer
import ai.koog.serialization.KotlinTypeToken
import ai.koog.serialization.TypeToken
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.jvm.javaType

/**
 * Serializer that uses Jackson (jackson-databind).
 *
 * @param objectMapper the Jackson [ObjectMapper] to use for serialization/deserialization.
 *                     The module [JSONElementModule] will be automatically registered to properly handle [JSONElement] types.
 */
public class JacksonSerializer(
    public val objectMapper: ObjectMapper,
) : KoogSerializer {

    init {
        // Register JSONElementModule to handle JSONElement serialization/deserialization
        objectMapper.registerModule(JSONElementModule())
    }

    override fun <T> serialize(value: T, typeToken: TypeToken): String {
        return objectMapper.writeValueAsString(value)
    }

    override fun <T> deserialize(value: String, typeToken: TypeToken): T {
        val javaType = resolveJavaType(typeToken)
        val result: Any? = objectMapper.readValue(value, javaType)

        // Handle JSONElement null case: Jackson returns Java null for JSON null,
        // but we need to return JSONNull singleton
        @Suppress("UNCHECKED_CAST")
        return when {
            result == null && JSONElement::class.java.isAssignableFrom(javaType.rawClass) -> JSONNull as T
            result == null && JSONPrimitive::class.java.isAssignableFrom(javaType.rawClass) -> JSONNull as T
            else -> result as T
        }
    }

    override fun <T> serializeToJSONElement(value: T, typeToken: TypeToken): JSONElement {
        val jsonNode = objectMapper.valueToTree<JsonNode>(value)
        return jsonNode.toJSONElement()
    }

    override fun <T> deserializeFromJSONElement(value: JSONElement, typeToken: TypeToken): T {
        val jsonNode = value.toJacksonJsonNode()
        val javaType = resolveJavaType(typeToken)
        @Suppress("UNCHECKED_CAST")
        return objectMapper.treeToValue(jsonNode, javaType.rawClass) as T
    }

    private fun resolveJavaType(typeToken: TypeToken): JavaType = when (typeToken) {
        is KotlinTypeToken -> objectMapper.typeFactory.constructType(typeToken.type.javaType)
        is JavaTypeToken -> objectMapper.typeFactory.constructType(typeToken.type)
    }
}
