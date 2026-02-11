package ai.koog.serialization

/**
 * Serializer for converting objects to and from JSON.
 */
public interface KoogSerializer {
    /**
     * Serializes a value to its JSON representation.
     *
     * @param value the object to serialize
     * @return JSON string representation of the serialized value
     */
    public fun <T> serialize(value: T, token: TypeToken): String

    /**
     * Deserializes a JSON string back to an object.
     *
     * @param value the JSON string to deserialize
     * @param token type token describing the target type
     * @return the deserialized object of type [T]
     */
    public fun <T> deserialize(value: String, token: TypeToken): T
}
