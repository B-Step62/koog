package ai.koog.serialization.kotlinx

import ai.koog.serialization.JavaTypeToken
import ai.koog.serialization.KSerializerTypeToken
import ai.koog.serialization.KotlinTypeToken
import ai.koog.serialization.TypeToken
import ai.koog.serialization.annotations.InternalKoogSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

@OptIn(InternalKoogSerializationApi::class)
internal actual fun <T> kotlinxSerializer(typeToken: TypeToken): KSerializer<T> {
    val serializer = when (typeToken) {
        is KotlinTypeToken -> serializer(typeToken.type)
        is JavaTypeToken -> serializer(typeToken.type)
        is KSerializerTypeToken<*> -> typeToken.serializer
    }

    @Suppress("UNCHECKED_CAST")
    return serializer as KSerializer<T>
}
