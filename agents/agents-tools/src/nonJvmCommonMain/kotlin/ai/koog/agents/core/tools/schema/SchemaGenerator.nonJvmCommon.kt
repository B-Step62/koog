package ai.koog.agents.core.tools.schema

import ai.koog.serialization.KSerializerTypeToken
import ai.koog.serialization.KotlinTypeToken
import ai.koog.serialization.TypeToken
import ai.koog.serialization.annotations.InternalKoogSerializationApi
import kotlinx.schema.json.JsonSchema
import kotlinx.serialization.serializerOrNull

@OptIn(InternalKoogSerializationApi::class)
internal actual fun getJsonSchema(typeToken: TypeToken): JsonSchema = when (typeToken) {
    is KotlinTypeToken -> {
        val descriptor = serializerOrNull(typeToken.type)?.descriptor
            ?: throw IllegalArgumentException(
                """
                KSerializer for ${typeToken.type} not found.
                On non-JVM platforms, automatic JSON schema generations is supported only for classes annotated with @kotlinx.serialization.Serializable
                """.trimIndent()
            )

        serializationGenerator.generateSchema(descriptor)
    }

    is KSerializerTypeToken<*> -> serializationGenerator.generateSchema(typeToken.serializer.descriptor)
}
