package ai.koog.agents.core.tools.schema

import ai.koog.serialization.JavaTypeToken
import ai.koog.serialization.KSerializerTypeToken
import ai.koog.serialization.KotlinTypeToken
import ai.koog.serialization.TypeToken
import ai.koog.serialization.annotations.InternalKoogSerializationApi
import kotlinx.schema.generator.json.ReflectionClassJsonSchemaGenerator
import kotlinx.schema.generator.json.serialization.SerializationClassJsonSchemaGenerator
import kotlinx.schema.json.JsonSchema
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KClass

private val serializationGenerator by lazy { SerializationClassJsonSchemaGenerator.Default }
private val reflectionGenerator by lazy { ReflectionClassJsonSchemaGenerator.Default }

@OptIn(InternalKoogSerializationApi::class)
internal actual fun getJsonSchema(typeToken: TypeToken): JsonSchema = when (typeToken) {
    // Try to find SerialDescriptor, fallback to reflection
    is KotlinTypeToken -> {
        val descriptor = serializerOrNull(typeToken.type)?.descriptor

        if (descriptor != null) {
            serializationGenerator.generateSchema(descriptor)
        } else {
            val kClass = typeToken.type.classifier as? KClass<*>
                ?: throw IllegalArgumentException(
                    "Can't generate JSON schema using reflection for ${typeToken.type} since it doesn't represent a Kotlin class"
                )

            reflectionGenerator.generateSchema(kClass)
        }
    }

    // Just use SerialDescriptor
    is KSerializerTypeToken<*> -> serializationGenerator.generateSchema(typeToken.serializer.descriptor)

    // Check that it's not a generic class and use reflection
    is JavaTypeToken -> {
        val kClass = when (val type = typeToken.type) {
            is Class<*> -> type.kotlin

            else -> throw IllegalArgumentException(
                "Unsupported Java type for schema generation: ${typeToken.type}. Only non-generic classes are supported"
            )
        }

        reflectionGenerator.generateSchema(kClass)
    }
}
