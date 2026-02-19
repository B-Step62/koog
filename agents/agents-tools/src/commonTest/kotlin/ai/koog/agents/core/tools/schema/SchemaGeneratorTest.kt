package ai.koog.agents.core.tools.schema

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test

class SchemaGeneratorTest {
    @Serializable
    @SerialName("TestClass")
    @LLMDescription("A test class")
    data class TestClass(
        @property:LLMDescription("A string property")
        val stringProperty: String,
        val intProperty: Int,
        val longProperty: Long,
        val doubleProperty: Double,
        val floatProperty: Float,
        val booleanProperty: Boolean?,
        val nullableProperty: String? = null,
        val listProperty: List<String> = emptyList(),
        val mapProperty: Map<String, Int> = emptyMap(),
        val nested: NestedProperty,
        val nestedList: List<NestedProperty> = emptyList(),
        val nestedMap: Map<String, NestedProperty> = emptyMap(),
        val polymorphicProperty: TestClosedPolymorphism,
        val enumProperty: TestEnum,
        val objectProperty: TestObject,
    )

    @Serializable
    @SerialName("NestedProperty")
    @LLMDescription("Nested property class")
    data class NestedProperty(
        @property:LLMDescription("Nested foo property")
        val foo: String,
        val bar: Int
    )

    @Serializable
    @SerialName("TestClosedPolymorphism")
    sealed class TestClosedPolymorphism {
        abstract val id: String

        @Suppress("unused")
        @Serializable
        @SerialName("ClosedSubclass1")
        data class SubClass1(
            override val id: String,
            val property1: String
        ) : TestClosedPolymorphism()

        @Suppress("unused")
        @Serializable
        @SerialName("ClosedSubclass2")
        data class SubClass2(
            override val id: String,
            val property2: Int,
            // This property produces StackOverflowError when generating the schema
            // https://github.com/Kotlin/kotlinx-schema/issues/192
            // val recursiveTypeProperty: TestClosedPolymorphism,
        ) : TestClosedPolymorphism()
    }

    @Suppress("unused")
    enum class TestEnum {
        One,
        Two
    }

    @SerialName("TestObject")
    @Serializable
    data object TestObject

    @Test
    fun test() {
        val testDescriptor = getToolDescriptor(
            argsTypeToken = typeToken<TestClass>(),
            toolName = "test_tool",
            toolDescription = "Test tool description",
        )
        println(testDescriptor)
//        val testSchema = getJsonSchema(typeToken<TestClass>())
//        println(testSchema.encodeToString(json = Json { prettyPrint = true } ))
    }
}
