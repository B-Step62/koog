@file:Suppress("ktlint:standard:function-naming", "FunctionName")

package ai.koog.serialization

import ai.koog.serialization.kotlinx.JSONArraySerializer
import ai.koog.serialization.kotlinx.JSONElementSerializer
import ai.koog.serialization.kotlinx.JSONLiteralSerializer
import ai.koog.serialization.kotlinx.JSONNullSerializer
import ai.koog.serialization.kotlinx.JSONObjectSerializer
import ai.koog.serialization.kotlinx.JSONPrimitiveSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlin.jvm.JvmStatic

/**
 * Serialization-library agnostic representation of a JSON element.
 *
 * This sealed hierarchy provides a dynamic JSON model that can be constructed
 * and manipulated independently of any specific JSON serialization library.
 */
@Serializable(with = JSONElementSerializer::class)
public sealed interface JSONElement

/**
 * JSON object with key-value pairs.
 *
 * @property entries map of string keys to JSON elements
 */
@Serializable(with = JSONObjectSerializer::class)
public data class JSONObject(
    val entries: Map<String, JSONElement>
) : JSONElement

/**
 * JSON array containing an ordered list of elements.
 *
 * @property elements list of JSON elements
 */
@Serializable(with = JSONArraySerializer::class)
public data class JSONArray(
    val elements: List<JSONElement>
) : JSONElement

/**
 * JSON primitive value (string, number, boolean, or null).
 */
@Serializable(with = JSONPrimitiveSerializer::class)
public sealed interface JSONPrimitive : JSONElement {
    /**
     * Raw string content of this primitive.
     */
    public val content: String

    /**
     * Whether this primitive is a JSON string type, i.e., is quoted, or not.
     */
    public val isString: Boolean

    /**
     * Returns content as string, or null if this is [JSONNull].
     */
    public val contentOrNull: String? get() = if (this is JSONNull) null else content

    /**
     * Attempts to parse content as [Int], returns null on failure.
     */
    public val intOrNull: Int? get() = content.toIntOrNull()

    /**
     * Attempts to parse content as [Long], returns null on failure.
     */
    public val longOrNull: Long? get() = content.toLongOrNull()

    /**
     * Attempts to parse content as [Double], returns null on failure.
     */
    public val doubleOrNull: Double? get() = content.toDoubleOrNull()

    /**
     * Attempts to parse content as [Boolean], returns null on failure.
     */
    public val booleanOrNull: Boolean? get() = content.toBooleanStrictOrNull()

    /**
     * Factory methods for creating JSON primitives.
     */
    public companion object {
        /**
         * Creates a JSON string primitive.
         */
        @JvmStatic
        public fun of(value: String): JSONLiteral = JSONLiteral(value, isString = true)

        /**
         * Creates a JSON number primitive from an [Int].
         */
        @JvmStatic
        public fun of(value: Int): JSONLiteral = JSONLiteral(value.toString(), isString = false)

        /**
         * Creates a JSON number primitive from a [Long].
         */
        @JvmStatic
        public fun of(value: Long): JSONLiteral = JSONLiteral(value.toString(), isString = false)

        /**
         * Creates a JSON number primitive from a [Double].
         */
        @JvmStatic
        public fun of(value: Double): JSONLiteral = JSONLiteral(value.toString(), isString = false)

        /**
         * Creates a JSON boolean primitive.
         */
        @JvmStatic
        public fun of(value: Boolean): JSONLiteral = JSONLiteral(value.toString(), isString = false)

        /**
         * Creates an unquoted JSON literal for raw JSON encoding.
         *
         * Use this for encoding values that cannot be represented using standard [JSONPrimitive] functions:
         * - Precise numeric values (avoiding floating-point precision errors)
         * - Large numbers beyond standard numeric types
         * - Raw JSON fragments
         *
         * Modeled after [JsonUnquotedLiteral] from kotlinx-serialization.
         */
        @JvmStatic
        public fun ofUnquoted(value: String): JSONLiteral = JSONLiteral(value, isString = false)
    }
}

/**
 * JSON literal value (string, number, or boolean).
 *
 * @property content raw string content of the literal
 * @property isString whether this represents a JSON string type
 */
@Serializable(with = JSONLiteralSerializer::class)
public data class JSONLiteral(
    override val content: String,
    override val isString: Boolean
) : JSONPrimitive

/**
 * JSON null value.
 */
@Serializable(with = JSONNullSerializer::class)
public data object JSONNull : JSONPrimitive {
    override val content: String = "null"
    override val isString: Boolean = false
}

/**
 * Factory function for creating a JSON string primitive.
 */
public fun JSONPrimitive(value: String): JSONLiteral = JSONPrimitive.of(value)

/**
 * Factory function for creating a JSON number primitive from an [Int].
 */
public fun JSONPrimitive(value: Int): JSONLiteral = JSONPrimitive.of(value)

/**
 * Factory function for creating a JSON number primitive from a [Long].
 */
public fun JSONPrimitive(value: Long): JSONLiteral = JSONPrimitive.of(value)

/**
 * Factory function for creating a JSON number primitive from a [Double].
 */
public fun JSONPrimitive(value: Double): JSONLiteral = JSONPrimitive.of(value)

/**
 * Factory function for creating a JSON boolean primitive.
 */
public fun JSONPrimitive(value: Boolean): JSONLiteral = JSONPrimitive.of(value)

/**
 * Factory function for creating an unquoted JSON literal for raw JSON encoding.
 */
public fun JSONUnquotedPrimitive(value: String): JSONLiteral = JSONPrimitive.ofUnquoted(value)
