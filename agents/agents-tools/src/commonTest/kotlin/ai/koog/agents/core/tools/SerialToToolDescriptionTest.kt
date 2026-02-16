package ai.koog.agents.core.tools

import ai.koog.agents.core.tools.annotations.InternalAgentToolsApi
import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(InternalAgentToolsApi::class)
class SerialToToolDescriptionTest {

    // ---------- Helper models ----------

    @Serializable
    @LLMDescription("Person description")
    data class Person(
        val name: String,
        val age: Int = 0, // optional due to default
        val nickname: String? = null, // optional due to default
        val address: Address, // required
    )

    @Serializable
    data class Address(
        val street: String
    )

    @Serializable
    enum class Color { RED, GREEN, BLUE }

    @Serializable
    object Singleton

    @Serializable
    data class FreeFormHolder(
        // contextual => free-form property mapping
        @Contextual val meta: Any? = null
    )

    // ---------- Tests ----------

    @Test
    fun class_mapping_collects_required_and_optional_and_uses_class_description_for_fields() {
        val personDesc = Person.serializer().descriptor.asToolDescriptor("person")

        // Top-level tool info
        assertEquals("person", personDesc.name)
        assertEquals("Person description", personDesc.description)

        // Required vs optional
        val requiredNames = personDesc.requiredParameters.map { it.name }.sorted()
        val optionalNames = personDesc.optionalParameters.map { it.name }.sorted()
        assertEquals(listOf("address", "name"), requiredNames)
        assertEquals(listOf("age", "nickname"), optionalNames)

        // Property descriptions currently mirror class-level description per implementation
        (personDesc.requiredParameters + personDesc.optionalParameters).forEach { param ->
            assertEquals(param.name, param.description)
        }

        // Nested object type for address
        val addressParam = personDesc.requiredParameters.first { it.name == "address" }
        val addressType = assertIs<ToolParameterType.Object>(addressParam.type)
        val addressPropNames = addressType.properties.map { it.name }
        assertEquals(listOf("street"), addressPropNames)
        assertEquals(listOf("street"), addressType.requiredProperties)
        assertEquals(false, addressType.additionalProperties)
        assertEquals(null, addressType.additionalPropertiesType)
    }

    @Serializable
    @LLMDescription(
        "Finish tool to compile final plan suggestion for the user's request. \n" +
            "Call to provide the final plan suggestion result."
    )
    data class TripPlan(
        @property:LLMDescription("The steps in the user travel plan.")
        val steps: List<Step>,
    ) {
        @Serializable
        @LLMDescription("The steps in the user travel plan.")
        data class Step(
            @property:LLMDescription("The location of the destination (e.g. city name)")
            val location: String,
            @property:LLMDescription("ISO 3166-1 alpha-2 country code of the location (e.g. US, GB, FR).")
            val countryCodeISO2: String? = null,
            @property:LLMDescription("Start date when the user arrives in this location in the ISO format, e.g. 2022-01-01.")
            val fromDate: LocalDate,
            @property:LLMDescription("End date when the user leaves this location in the ISO format, e.g. 2022-01-01.")
            val toDate: LocalDate,
            @property:LLMDescription("More information about this step from the plan")
            val description: String
        )
    }

    val expectedTripPlanToolDescriptor = ToolDescriptor(
        name = "provideTripPlan",
        description = """
            Finish tool to compile final plan suggestion for the user's request. 
            Call to provide the final plan suggestion result.
        """.trimIndent(),
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "steps",
                description = "The steps in the user travel plan.",
                type = ToolParameterType.List(
                    ToolParameterType.Object(
                        properties = listOf(
                            ToolParameterDescriptor(
                                name = "location",
                                description = "The location of the destination (e.g. city name)",
                                type = ToolParameterType.String
                            ),
                            ToolParameterDescriptor(
                                name = "countryCodeISO2",
                                description = "ISO 3166-1 alpha-2 country code of the location (e.g. US, GB, FR).",
                                type = ToolParameterType.String
                            ),
                            ToolParameterDescriptor(
                                name = "fromDate",
                                description = "Start date when the user arrives in this location in the ISO format, e.g. 2022-01-01.",
                                type = ToolParameterType.String
                            ),
                            ToolParameterDescriptor(
                                name = "toDate",
                                description = "End date when the user leaves this location in the ISO format, e.g. 2022-01-01.",
                                type = ToolParameterType.String
                            ),
                            ToolParameterDescriptor(
                                name = "description",
                                description = "More information about this step from the plan",
                                type = ToolParameterType.String
                            )
                        ),
                        requiredProperties = listOf(
                            "location",
                            "fromDate",
                            "toDate",
                            "description"
                        ),
                        additionalProperties = false
                    )
                ),
            )
        )
    )

    @Test
    fun verify_class_with_array_of_nested_objects_tool_descriptor_generation() {
        val tripPlanDescriptor = serializer<TripPlan>().descriptor.asToolDescriptor("provideTripPlan")

        assertEquals(expectedTripPlanToolDescriptor, tripPlanDescriptor)
    }

    @Test
    fun verify_optional_description_applies() {
        val tripPlanDescriptor = serializer<TripPlan>().descriptor.asToolDescriptor(
            toolName = "provideTripPlan",
            toolDescription = "Custom tool, call me!"
        )

        assertEquals("Custom tool, call me!", tripPlanDescriptor.description)
        assertEquals(expectedTripPlanToolDescriptor.copy(description = "Custom tool, call me!"), tripPlanDescriptor)
    }
}
