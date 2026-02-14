@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package ai.koog.serialization

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Platform-agnostic type token for runtime type representation.
 *
 * Sealed interface with platform-specific implementations to enable exhaustive when expressions.
 */
public expect sealed interface TypeToken

/**
 * Common type token implementation based on [KType].
 */
public class KotlinTypeToken(
    public val type: KType,
) : TypeToken

/**
 * Temporary used during migration from [kotlinx.serialization.KSerializer] to [TypeToken] in public APIs.
 */
// TODO finalize the migration and remove
@InternalSerializationApi
public class KSerializerTypeToken<T>(
    public val serializer: KSerializer<T>
) : TypeToken

/**
 * Creates a [KotlinTypeToken] from a Kotlin [KType].
 */
public fun typeToken(type: KType): KotlinTypeToken = KotlinTypeToken(type)

/**
 * Creates a [KotlinTypeToken] from [T]
 */
public inline fun <reified T> typeToken(): KotlinTypeToken = typeToken(typeOf<T>())
