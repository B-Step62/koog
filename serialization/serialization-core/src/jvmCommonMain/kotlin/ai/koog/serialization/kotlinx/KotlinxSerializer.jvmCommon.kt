package ai.koog.serialization.kotlinx

import ai.koog.serialization.JavaTypeToken
import ai.koog.serialization.KotlinTypeToken
import ai.koog.serialization.TypeToken
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

internal actual fun <T> kotlinxSerializer(typeToken: TypeToken): KSerializer<T> {
    val serializer = when (typeToken) {
        is KotlinTypeToken -> serializer(typeToken.type)
        is JavaTypeToken -> serializer(typeToken.type)
    }

    @Suppress("UNCHECKED_CAST")
    return serializer as KSerializer<T>
}
