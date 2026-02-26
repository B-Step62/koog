package ai.koog.agents.core.tools.schema

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.serialization.JavaTypeToken
import ai.koog.serialization.KSerializerTypeToken
import ai.koog.serialization.KotlinTypeToken
import ai.koog.serialization.TypeToken
import ai.koog.serialization.annotations.InternalKoogSerializationApi
import kotlinx.schema.generator.json.ReflectionClassJsonSchemaGenerator
import kotlinx.schema.generator.json.ReflectionFunctionCallingSchemaGenerator
import kotlinx.schema.json.JsonSchema
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

private val reflectionClassGenerator by lazy { ReflectionClassJsonSchemaGenerator.Default }
private val reflectionFunctionGenerator by lazy { ReflectionFunctionCallingSchemaGenerator.Default }

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

            reflectionClassGenerator.generateSchema(kClass)
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

        reflectionClassGenerator.generateSchema(kClass)
    }
}

/**
 * Generates a [ToolDescriptor] by generating and converting the function calling schema for the provided [callable]
 */
internal fun getToolDescriptor(
    callable: KCallable<*>,
    toolName: String,
    toolDescription: String? = null,
): ToolDescriptor {
    val schema = reflectionFunctionGenerator.generateSchema(callable)

    // All parameters for function calling are considered required
    val requiredParameters = schema.parameters.properties
        .orEmpty()
        .map { (name, property) ->
            ToolParameterDescriptor(
                name = name,
                description = property.descriptionOrEmpty,
                type = property.toToolParameterType(defs = null) // no defs in function calling schema
            )
        }

    return ToolDescriptor(
        name = toolName,
        description = toolDescription ?: schema.description.orEmpty(),
        requiredParameters = requiredParameters,
    )
}
