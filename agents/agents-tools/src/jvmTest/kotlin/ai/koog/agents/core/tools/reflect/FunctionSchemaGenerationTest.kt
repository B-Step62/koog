package ai.koog.agents.core.tools.reflect

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.schema.JavaTestFunction
import kotlinx.schema.generator.json.ReflectionFunctionCallingSchemaGenerator
import kotlinx.serialization.json.Json
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction
import kotlin.test.Test

class FunctionSchemaGenerationTest {
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

    @LLMDescription("Nested property class")
    data class NestedProperty(
        @property:LLMDescription("Nested foo property")
        val foo: String,
        val bar: Int
    )

    sealed class TestClosedPolymorphism {
        abstract val id: String

        @Suppress("unused")
        data class SubClass1(
            override val id: String,
            val property1: String
        ) : TestClosedPolymorphism()

        @Suppress("unused")
        data class SubClass2(
            override val id: String,
            val property2: Int,
        ) : TestClosedPolymorphism()
    }

    @Suppress("unused")
    enum class TestEnum {
        One,
        Two
    }

    data object TestObject

    @LLMDescription("Sample function")
    fun sampleFunction(
        @LLMDescription("Sample parameter")
        a: String,
        @LLMDescription("Another sample parameter")
        b: TestClass? = null,
    ): String {
        return ""
    }

    val generator = ReflectionFunctionCallingSchemaGenerator(
        json = Json { prettyPrint = true }
    )

    @Test
    fun test() {
        val schema = generator.generateSchemaString(::sampleFunction)
        println(schema)
    }

    @Test
    fun javaTest() {
        val schema = generator.generateSchemaString(
            JavaTestFunction.FUNCTION.kotlinFunction as KFunction<*>
        )
        println(schema)
    }
}
