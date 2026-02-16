package ai.koog.agents.core.tools

import ai.koog.agents.core.tools.annotations.InternalAgentToolsApi
import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.descriptors.elementNames

/**
 * Converts a [SerialDescriptor] into a [ToolDescriptor] with metadata about a tool,
 * including its name, description, and parameters.
 *
 * @param toolName The name to assign to the resulting tool descriptor.
 * @param toolDescription An optional custom description for the tool. Defaults to the descriptor's annotation-based description if null.
 * @return A [ToolDescriptor] representing the tool's schema, including its name, description, and any parameters.
 */
@InternalAgentToolsApi
public fun SerialDescriptor.asToolDescriptor(
    toolName: String,
    toolDescription: String? = null,
): ToolDescriptor {
    val description =
        toolDescription ?: annotations.findLLMDescription().orEmpty()

    return when (kind) {
        // Tool with args
        StructureKind.CLASS -> {
            val (properties, required) = parameterDescriptors().let { it.descriptors to it.required }

            ToolDescriptor(
                name = toolName,
                description = description,
                requiredParameters = properties.filter { required.contains(it.name) },
                optionalParameters = properties.filterNot { required.contains(it.name) }
            )
        }

        // Tool without args
        StructureKind.OBJECT -> ToolDescriptor(
            name = toolName,
            description = description,
        )

        else -> throw IllegalArgumentException("Only classes are supported as tool schemas, got $kind")
    }
}

private class ParameterDescriptors(
    val descriptors: List<ToolParameterDescriptor>,
    val required: List<String>,
)

private fun SerialDescriptor.parameterDescriptors(): ParameterDescriptors {
    val required = mutableListOf<String>()

    val descriptors = List(elementsCount) { i ->
        val name = getElementName(i)
        val descriptor = getElementDescriptor(i)
        val isOptional = isElementOptional(i) || descriptor.isNullable

        if (!isOptional) {
            required.add(name)
        }

        ToolParameterDescriptor(
            name = name,
            description = getElementAnnotations(i).findLLMDescription() ?: name,
            type = getElementDescriptor(i).toToolParameterType()
        )
    }

    return ParameterDescriptors(descriptors, required)
}

private fun SerialDescriptor.toToolParameterType(): ToolParameterType = when (kind) {
    PrimitiveKind.CHAR,
    PrimitiveKind.STRING -> ToolParameterType.String

    PrimitiveKind.BOOLEAN -> ToolParameterType.Boolean

    PrimitiveKind.BYTE,
    PrimitiveKind.SHORT,
    PrimitiveKind.INT,
    PrimitiveKind.LONG -> ToolParameterType.Integer

    PrimitiveKind.FLOAT,
    PrimitiveKind.DOUBLE -> ToolParameterType.Float

    StructureKind.LIST -> ToolParameterType.List(getElementDescriptor(0).toToolParameterType())

    SerialKind.ENUM -> ToolParameterType.Enum(Array(elementsCount, ::getElementName))

    StructureKind.CLASS -> {
        val (properties, required) = parameterDescriptors().let { it.descriptors to it.required }

        ToolParameterType.Object(
            properties = properties,
            requiredProperties = required,
            additionalProperties = false
        )
    }

    PolymorphicKind.SEALED -> {
        // Check that "value" element containing subclasses descriptors is present
        require(elementNames.toList().getOrNull(1) == "value") {
            "Expected second element to be 'value', got: ${elementNames.toList()}"
        }

        val subclassesDescriptor = elementDescriptors
            .toList()
            .getOrNull(1)
            ?: throw IllegalArgumentException("Cannot find subclasses descriptor")

        ToolParameterType.AnyOf(
            types = subclassesDescriptor.elementDescriptors
                .map { descriptor ->
                    val (properties, required) = descriptor.parameterDescriptors().let { it.descriptors to it.required }

                    ToolParameterDescriptor(
                        // it's redundant here as it is ignored during schema generation, see comment on ToolParameterType.AnyOf
                        name = "",
                        // type description, if present
                        description = descriptor.annotations
                            .findLLMDescription()
                            .orEmpty(),
                        type = ToolParameterType.Object(
                            properties = properties,
                            requiredProperties = required,
                            additionalProperties = false
                        )
                    )
                }
                .toTypedArray()
        )
    }

    else -> throw IllegalArgumentException("Unsupported descriptor type: $kind")
}

private fun List<Annotation>.findLLMDescription(): String? =
    filterIsInstance<LLMDescription>().firstOrNull()?.description
