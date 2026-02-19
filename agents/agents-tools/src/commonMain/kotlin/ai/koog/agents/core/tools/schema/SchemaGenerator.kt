package ai.koog.agents.core.tools.schema

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import ai.koog.serialization.TypeToken
import kotlinx.schema.json.AdditionalPropertiesSchema
import kotlinx.schema.json.AllowAdditionalProperties
import kotlinx.schema.json.AnyOfPropertyDefinition
import kotlinx.schema.json.ArrayPropertyDefinition
import kotlinx.schema.json.BooleanPropertyDefinition
import kotlinx.schema.json.CommonSchemaAttributes
import kotlinx.schema.json.DenyAdditionalProperties
import kotlinx.schema.json.JsonSchema
import kotlinx.schema.json.JsonSchemaConstants
import kotlinx.schema.json.NumericPropertyDefinition
import kotlinx.schema.json.ObjectPropertyDefinition
import kotlinx.schema.json.OneOfPropertyDefinition
import kotlinx.schema.json.PropertyDefinition
import kotlinx.schema.json.ReferencePropertyDefinition
import kotlinx.schema.json.StringPropertyDefinition
import kotlinx.schema.json.ValuePropertyDefinition

internal expect fun getJsonSchema(typeToken: TypeToken): JsonSchema

/**
 * Generates a [ToolDescriptor] by generating and converting the JSON schema for the type defined by the provided [argsTypeToken]
 */
internal fun getToolDescriptor(
    argsTypeToken: TypeToken,
    toolName: String,
    toolDescription: String? = null,
): ToolDescriptor {
    val schema = getJsonSchema(argsTypeToken)

    if (JsonSchemaConstants.Types.OBJECT !in schema.type) {
        throw IllegalArgumentException("Only objects are supported as tool schemas, got ${schema.type}")
    }

    val (requiredParameters, optionalParameters) = schema.properties
        .map { (name, property) ->
            ToolParameterDescriptor(
                name = name,
                description = (property as? CommonSchemaAttributes)?.description.orEmpty(),
                type = property.toToolParameterType(schema)
            )
        }
        .partition { it.name in schema.required }

    return ToolDescriptor(
        name = toolName,
        description = toolDescription ?: schema.description.orEmpty(),
        requiredParameters = requiredParameters,
        optionalParameters = optionalParameters,
    )
}

/**
 * Converts a JSON schema property representation [PropertyDefinition] to our tool parameter representation [ToolParameterType].
 * @param schema JSON schema for which the whole tool descriptor generation is performed.
 */
private fun PropertyDefinition.toToolParameterType(schema: JsonSchema): ToolParameterType = when (this) {
    is ValuePropertyDefinition<*> -> {
        val type = this.type
            ?.takeIf { it.isNotEmpty() }
            ?: throw IllegalArgumentException("Value property definition is missing the 'type' (either null or empty)")

        val isNullableType = JsonSchemaConstants.Types.NULL in type

        val parameterType = when (this) {
            is StringPropertyDefinition ->
                ToolParameterType.String

            is BooleanPropertyDefinition ->
                ToolParameterType.Boolean

            is NumericPropertyDefinition -> when {
                JsonSchemaConstants.Types.INTEGER in type -> ToolParameterType.Integer
                JsonSchemaConstants.Types.NUMBER in type -> ToolParameterType.Float
                else -> throw IllegalArgumentException("Unsupported numeric type: $type")
            }

            is ArrayPropertyDefinition -> {
                ToolParameterType.List(
                    itemsType = items?.toToolParameterType(schema)
                        ?: throw IllegalArgumentException("Array property definition is missing the 'items' type")
                )
            }

            is ObjectPropertyDefinition -> {
                ToolParameterType.Object(
                    properties = properties
                        .orEmpty()
                        .map { (name, property) ->
                            ToolParameterDescriptor(
                                name = name,
                                description = (property as? CommonSchemaAttributes)?.description.orEmpty(),
                                type = property.toToolParameterType(schema)
                            )
                        },
                    requiredProperties = required.orEmpty(),
                    additionalProperties = when (additionalProperties) {
                        is AllowAdditionalProperties, is AdditionalPropertiesSchema -> true
                        is DenyAdditionalProperties, null -> false
                    },
                    additionalPropertiesType = (additionalProperties as? AdditionalPropertiesSchema)?.schema
                        ?.toToolParameterType(schema),
                )
            }

            else ->
                throw IllegalArgumentException("Unsupported value property definition type: $this")
        }

        if (isNullableType) {
            // emulate type union
            ToolParameterType.AnyOf(
                types = arrayOf(
                    ToolParameterDescriptor(type = ToolParameterType.Null, name = "", description = ""),
                    ToolParameterDescriptor(type = parameterType, name = "", description = ""),
                )
            )
        } else {
            parameterType
        }
    }

    is StringPropertyDefinition -> {
        val enum = this.enum

        type
        if (enum != null) {
            ToolParameterType.Enum(enum.toTypedArray())
        } else {
            ToolParameterType.String
        }
    }

    is ReferencePropertyDefinition -> {
        val ref = this.ref
            ?: throw IllegalArgumentException("Reference property definition is missing the 'ref' attribute")
        val defs = schema.defs
            ?: throw IllegalArgumentException("Encountered a ref in the JSON schema but the schema is missing the defs section")

        defs[ref.removePrefix(JsonSchemaConstants.Keys.REF_PREFIX)]
            ?.toToolParameterType(schema)
            ?: throw IllegalArgumentException("Can't find ref in defs: $ref. Schema defs: ${defs.keys}")
    }

    is AnyOfPropertyDefinition -> {
        ToolParameterType.AnyOf(
            types = anyOf
                .map { ToolParameterDescriptor(type = it.toToolParameterType(schema), name = "", description = "") }
                .toTypedArray()
        )
    }

    // It isn't fully correct, but to keep the compatibility with ToolDescriptor for now consider oneOf == anyOf
    is OneOfPropertyDefinition -> {
        ToolParameterType.AnyOf(
            types = oneOf
                .map { ToolParameterDescriptor(type = it.toToolParameterType(schema), name = "", description = "") }
                .toTypedArray()
        )
    }

    else ->
        throw IllegalArgumentException("Unsupported property definition type: $this")
}
