package ai.koog.serialization.kotlinx

import ai.koog.serialization.JSONArray
import ai.koog.serialization.JSONElement
import ai.koog.serialization.JSONLiteral
import ai.koog.serialization.JSONNull
import ai.koog.serialization.JSONObject
import ai.koog.serialization.JSONPrimitive
import ai.koog.serialization.JSONUnquotedPrimitive
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

/**
 * Detailed test for [KotlinxSerializer] with [JSONElement] serialization
 */
class KotlinxJSONElementSerializationTest {
    private val serializer = KotlinxSerializer(Json)

    @Test
    fun testJSONNull() {
        val element = JSONNull
        //language=JSON
        val jsonString = "null"

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe element
    }

    @Test
    fun testJSONLiteralString() {
        val element = JSONPrimitive("hello")
        //language=JSON
        val jsonString = "\"hello\""

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe element
    }

    @Test
    fun testJSONLiteralNumber() {
        val element = JSONPrimitive(42)
        //language=JSON
        val jsonString = "42"

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe JSONLiteral("42", isString = false)
    }

    @Test
    fun testJSONLiteralBoolean() {
        val element = JSONPrimitive(true)
        //language=JSON
        val jsonString = "true"

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe JSONLiteral("true", isString = false)
    }

    @Test
    fun testJSONPrimitiveString() {
        val element = JSONPrimitive("world")
        //language=JSON
        val jsonString = "\"world\""

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe element
    }

    @Test
    fun testJSONPrimitiveNull() {
        val element: JSONPrimitive = JSONNull
        //language=JSON
        val jsonString = "null"

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe element
    }

    @Test
    fun testJSONArrayEmpty() {
        val element = JSONArray(emptyList())
        //language=JSON
        val jsonString = "[]"

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe element
    }

    @Test
    fun testJSONArrayWithPrimitives() {
        val element = JSONArray(
            listOf(
                JSONPrimitive(1),
                JSONPrimitive("test"),
                JSONPrimitive(true),
                JSONNull
            )
        )
        //language=JSON
        val jsonString = """
            [1, "test", true, null]
        """

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe JSONArray(
            listOf(
                JSONLiteral("1", isString = false),
                JSONLiteral("test", isString = true),
                JSONLiteral("true", isString = false),
                JSONNull
            )
        )
    }

    @Test
    fun testJSONObjectEmpty() {
        val element = JSONObject(emptyMap())
        //language=JSON
        val jsonString = "{}"

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe element
    }

    @Test
    fun testJSONObjectWithPrimitives() {
        val element = JSONObject(
            mapOf(
                "name" to JSONPrimitive("John"),
                "age" to JSONPrimitive(30),
                "active" to JSONPrimitive(true),
                "data" to JSONNull
            )
        )
        //language=JSON
        val jsonString = """
            {
              "name": "John",
              "age": 30,
              "active": true,
              "data": null
            }
        """

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe JSONObject(
            mapOf(
                "name" to JSONLiteral("John", isString = true),
                "age" to JSONLiteral("30", isString = false),
                "active" to JSONLiteral("true", isString = false),
                "data" to JSONNull
            )
        )
    }

    @Test
    fun testJSONElementNestedStructure() {
        val element = JSONObject(
            mapOf(
                "user" to JSONObject(
                    mapOf(
                        "name" to JSONPrimitive("Alice"),
                        "scores" to JSONArray(
                            listOf(
                                JSONPrimitive(95),
                                JSONPrimitive(87),
                                JSONPrimitive(92)
                            )
                        )
                    )
                ),
                "metadata" to JSONObject(
                    mapOf(
                        "version" to JSONPrimitive(1),
                        "nullable" to JSONNull
                    )
                )
            )
        )
        //language=JSON
        val jsonString = """
            {
              "user": {
                "name": "Alice",
                "scores": [95, 87, 92]
              },
              "metadata": {
                "version": 1,
                "nullable": null
              }
            }
        """

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe JSONObject(
            mapOf(
                "user" to JSONObject(
                    mapOf(
                        "name" to JSONLiteral("Alice", isString = true),
                        "scores" to JSONArray(
                            listOf(
                                JSONLiteral("95", isString = false),
                                JSONLiteral("87", isString = false),
                                JSONLiteral("92", isString = false)
                            )
                        )
                    )
                ),
                "metadata" to JSONObject(
                    mapOf(
                        "version" to JSONLiteral("1", isString = false),
                        "nullable" to JSONNull
                    )
                )
            )
        )
    }

    @Test
    fun testJSONElementArray() {
        val element = JSONArray(
            listOf(
                JSONObject(mapOf("id" to JSONPrimitive(1))),
                JSONObject(mapOf("id" to JSONPrimitive(2))),
                JSONObject(mapOf("id" to JSONPrimitive(3)))
            )
        )
        //language=JSON
        val jsonString = """
            [
              {"id": 1},
              {"id": 2},
              {"id": 3}
            ]
        """

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe JSONArray(
            listOf(
                JSONObject(mapOf("id" to JSONLiteral("1", isString = false))),
                JSONObject(mapOf("id" to JSONLiteral("2", isString = false))),
                JSONObject(mapOf("id" to JSONLiteral("3", isString = false)))
            )
        )
    }

    @Test
    fun testJSONUnquotedPrimitive() {
        val element = JSONUnquotedPrimitive("12345678901234567890")
        //language=JSON
        val jsonString = "12345678901234567890"

        serializer.serializeJSONElement(element) shouldEqualJson jsonString
        serializer.deserializeJSONElement(jsonString) shouldBe JSONLiteral("12345678901234567890", isString = false)
    }

    @Test
    fun testRoundTripSerialization() {
        val original: JSONElement = JSONObject(
            mapOf(
                "string" to JSONPrimitive("value"),
                "number" to JSONPrimitive(42.5),
                "boolean" to JSONPrimitive(false),
                "null" to JSONNull,
                "array" to JSONArray(listOf(JSONPrimitive(1), JSONPrimitive(2))),
                "nested" to JSONObject(mapOf("key" to JSONPrimitive("nested value")))
            )
        )

        val serialized = serializer.serializeJSONElement(original)
        val deserialized = serializer.deserializeJSONElement(serialized)

        deserialized shouldBe original
    }
}
