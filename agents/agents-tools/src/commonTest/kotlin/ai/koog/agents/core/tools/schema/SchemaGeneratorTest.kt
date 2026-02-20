package ai.koog.agents.core.tools.schema

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlinx.schema.json.encodeToString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
        val booleanNullableProperty: Boolean?,
        val nullableProperty: String? = null,
        val listProperty: List<String> = emptyList(),
        val mapProperty: Map<String, Int> = emptyMap(),
        val nestedProperty: NestedProperty = NestedProperty("foo", 1),
        val nestedListProperty: List<NestedProperty> = emptyList(),
        val nestedMapProperty: Map<String, NestedProperty> = emptyMap(),
        val polymorphicProperty: TestClosedPolymorphism = TestClosedPolymorphism.SubClass1("id1", "property1"),
        val enumProperty: TestEnum = TestEnum.One,
        val objectProperty: TestObject = TestObject,
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
        val toolName = "test_tool"
        val toolDescription = "Test tool description"

        val expectedDescriptor = ToolDescriptor(
            name = toolName,
            description = toolDescription,
            requiredParameters = listOf(
                ToolParameterDescriptor(
                    name = "stringProperty",
                    description = "A string property",
                    type = ToolParameterType.String,
                ),
                ToolParameterDescriptor(
                    name = "intProperty",
                    description = "",
                    type = ToolParameterType.Integer,
                ),
                ToolParameterDescriptor(
                    name = "longProperty",
                    description = "",
                    type = ToolParameterType.Integer,
                ),
                ToolParameterDescriptor(
                    name = "doubleProperty",
                    description = "",
                    type = ToolParameterType.Float,
                ),
                ToolParameterDescriptor(
                    name = "floatProperty",
                    description = "",
                    type = ToolParameterType.Float,
                ),
                ToolParameterDescriptor(
                    name = "booleanNullableProperty",
                    description = "",
                    type = ToolParameterType.AnyOf(
                        types = arrayOf(
                            ToolParameterDescriptor(type = ToolParameterType.Null, name = "", description = ""),
                            ToolParameterDescriptor(type = ToolParameterType.Boolean, name = "", description = ""),
                        )
                    )
                ),
            ),
            optionalParameters = listOf(
                ToolParameterDescriptor(
                    name = "nullableProperty",
                    description = "",
                    type = ToolParameterType.AnyOf(
                        types = arrayOf(
                            ToolParameterDescriptor(type = ToolParameterType.Null, name = "", description = ""),
                            ToolParameterDescriptor(type = ToolParameterType.String, name = "", description = ""),
                        )
                    )
                ),
                ToolParameterDescriptor(
                    name = "listProperty",
                    description = "",
                    type = ToolParameterType.List(ToolParameterType.String),
                ),
                ToolParameterDescriptor(
                    name = "mapProperty",
                    description = "",
                    type = ToolParameterType.Object(
                        properties = emptyList(),
                        additionalProperties = true,
                        additionalPropertiesType = ToolParameterType.Integer,
                    )
                ),
                ToolParameterDescriptor(
                    name = "nestedProperty",
                    description = "Nested property class",
                    type = ToolParameterType.Object(
                        properties = listOf(
                            ToolParameterDescriptor(
                                name = "foo",
                                description = "Nested foo property",
                                type = ToolParameterType.String,
                            ),
                            ToolParameterDescriptor(
                                name = "bar",
                                description = "",
                                type = ToolParameterType.Integer,
                            )
                        )
                    )
                ),
                ToolParameterDescriptor(
                    name = "nestedListProperty",
                    description = "",
                    type = ToolParameterType.List(
                        ToolParameterType.Object(
                            properties = listOf(
                                ToolParameterDescriptor(
                                    name = "foo",
                                    description = "Nested foo property",
                                    type = ToolParameterType.String,
                                ),
                                ToolParameterDescriptor(
                                    name = "bar",
                                    description = "",
                                    type = ToolParameterType.Integer,
                                )
                            )
                        )
                    )
                ),
                ToolParameterDescriptor(
                    name = "nestedMapProperty",
                    description = "",
                    type = ToolParameterType.Object(
                        properties = emptyList(),
                        additionalProperties = true,
                        additionalPropertiesType = ToolParameterType.Object(
                            properties = listOf(
                                ToolParameterDescriptor(
                                    name = "foo",
                                    description = "Nested foo property",
                                    type = ToolParameterType.String,
                                ),
                                ToolParameterDescriptor(
                                    name = "bar",
                                    description = "",
                                    type = ToolParameterType.Integer,
                                )
                            )
                        )
                    )
                ),
                ToolParameterDescriptor(
                    name = "polymorphicProperty",
                    description = "",
                    type = ToolParameterType.AnyOf(
                        types = arrayOf(
                            ToolParameterDescriptor(
                                type = ToolParameterType.Object(
                                    properties = listOf(
                                        ToolParameterDescriptor(
                                            name = "id",
                                            description = "",
                                            type = ToolParameterType.String,
                                        ),
                                        ToolParameterDescriptor(
                                            name = "property1",
                                            description = "",
                                            type = ToolParameterType.String,
                                        )
                                    )
                                ),
                                name = "", description = "",
                            ),
                            ToolParameterDescriptor(
                                type = ToolParameterType.Object(
                                    properties = listOf(
                                        ToolParameterDescriptor(
                                            name = "id",
                                            description = "",
                                            type = ToolParameterType.String,
                                        ),
                                        ToolParameterDescriptor(
                                            name = "property2",
                                            description = "",
                                            type = ToolParameterType.String,
                                        )
                                    )
                                ),
                                name = "", description = "",
                            ),
                        )
                    )
                ),
                ToolParameterDescriptor(
                    name = "enumProperty",
                    description = "",
                    type = ToolParameterType.Enum(arrayOf("One", "Two")),
                ),
                ToolParameterDescriptor(
                    name = "objectProperty",
                    description = "",
                    type = ToolParameterType.Object(properties = emptyList()),
                )
            ),
        )

        val actualDescriptor = getToolDescriptor(
            argsTypeToken = typeToken<TestClass>(),
            toolName = toolName,
            toolDescription = toolDescription,
        )

//        println(actualDescriptor)
//        assertEquals(expectedDescriptor, actualDescriptor)
        val testSchema = getJsonSchema(typeToken<TestClass>())
        println(testSchema.encodeToString(json = Json { prettyPrint = true } ))
    }
}
