package ai.koog.serialization

/**
 * Serializer for converting values to and from JSON.
 */
public interface KoogSerializer {
    /**
     * Serializes a value to its JSON representation.
     *
     * @param value value to serialize
     * @param typeToken token capturing type information of [T]
     * @return JSON string representation of the value
     */
    public fun <T> serialize(value: T, typeToken: TypeToken): String

    /**
     * Deserializes a JSON string to a value of type [T].
     *
     * @param value JSON string to deserialize
     * @param typeToken token capturing type information of [T]
     * @return deserialized value of type [T]
     */
    public fun <T> deserialize(value: String, typeToken: TypeToken): T

    /**
     * Serializes a value to its [JSONElement] representation.
     *
     * @param value value to serialize
     * @param typeToken token capturing type information of [T]
     * @return [JSONElement] representation of the value
     */
    public fun <T> serializeToJSONElement(value: T, typeToken: TypeToken): JSONElement

    /**
     * Deserializes [JSONElement] to a value of type [T].
     *
     * @param value [JSONElement] to deserialize.
     * @param typeToken token capturing type information of [T]
     * @return deserialized value of type [T]
     */
    public fun <T> deserializeFromJSONElement(value: JSONElement, typeToken: TypeToken): T

    /**
     * Serializes a [JSONElement] to its JSON string representation.
     *
     * @param value [JSONElement] to serialize
     * @return JSON string representation of the value
     */
    public fun serializeJSONElement(value: JSONElement): String =
        serialize(value, typeToken = typeToken<JSONElement>())

    /**
     * Deserializes a JSON string to a [JSONElement].
     *
     * @param value JSON string to deserialize
     * @return deserialized [JSONElement]
     */
    public fun deserializeJSONElement(value: String): JSONElement =
        deserialize(value, typeToken = typeToken<JSONElement>())
}
