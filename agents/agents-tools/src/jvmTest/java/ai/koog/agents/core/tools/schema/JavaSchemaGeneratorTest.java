package ai.koog.agents.core.tools.schema;

import ai.koog.agents.core.tools.ToolDescriptor;
import ai.koog.agents.core.tools.ToolParameterDescriptor;
import ai.koog.agents.core.tools.ToolParameterType;
import ai.koog.serialization.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// FIXME in kotlinx-schema: constructor parameters LLMDescription doesn't work
// fixed in https://github.com/Kotlin/kotlinx-schema/pull/203
public class JavaSchemaGeneratorTest {
    // @LLMDescription(description = "A test class")
    public static class TestClass {
        public TestClass(
            // @LLMDescription(description = "A string property")
            String stringProperty,
            int intProperty,
            long longProperty,
            double doubleProperty,
            float floatProperty,
            Boolean booleanNullableProperty,
            String nullableProperty,
            List<String> listProperty,
            Map<String, Integer> mapProperty,
            NestedProperty nestedProperty,
            List<NestedProperty> nestedListProperty,
            Map<String, NestedProperty> nestedMapProperty,
            TestEnum enumProperty
        ) {

        }
    }

    // @LLMDescription(description = "Nested property class")
    public static class NestedProperty {
        public NestedProperty(
            // @LLMDescription(description = "Nested foo property")
            String foo,
            int bar
        ) {

        }
    }

    public enum TestEnum {
        One,
        Two
    }

    @Test
    public void testGeneratesToolDescriptorFromJavaClass() {
        String toolName = "test_tool";
        String toolDescription = "Test tool description";

        ToolParameterType.Object nestedObject = new ToolParameterType.Object(
            Arrays.asList(
                new ToolParameterDescriptor("foo", "", ToolParameterType.String.INSTANCE),
                new ToolParameterDescriptor("bar", "", ToolParameterType.Integer.INSTANCE)
            ),
            Arrays.asList("foo", "bar"),
            false,
            null
        );

        ToolDescriptor expectedDescriptor = new ToolDescriptor(
            toolName,
            toolDescription,
            Arrays.asList(
                new ToolParameterDescriptor("stringProperty", "", ToolParameterType.String.INSTANCE),
                new ToolParameterDescriptor("intProperty", "", ToolParameterType.Integer.INSTANCE),
                new ToolParameterDescriptor("longProperty", "", ToolParameterType.Integer.INSTANCE),
                new ToolParameterDescriptor("doubleProperty", "", ToolParameterType.Float.INSTANCE),
                new ToolParameterDescriptor("floatProperty", "", ToolParameterType.Float.INSTANCE),
                new ToolParameterDescriptor("booleanNullableProperty", "", ToolParameterType.Boolean.INSTANCE),
                new ToolParameterDescriptor("nullableProperty", "", ToolParameterType.String.INSTANCE),
                new ToolParameterDescriptor("listProperty", "", new ToolParameterType.List(ToolParameterType.String.INSTANCE)),
                new ToolParameterDescriptor("mapProperty", "", new ToolParameterType.Object(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    true,
                    ToolParameterType.Integer.INSTANCE
                )),
                new ToolParameterDescriptor("nestedProperty", "", nestedObject),
                new ToolParameterDescriptor("nestedListProperty", "", new ToolParameterType.List(nestedObject)),
                new ToolParameterDescriptor("nestedMapProperty", "", new ToolParameterType.Object(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    true,
                    nestedObject
                )),
                new ToolParameterDescriptor("enumProperty", "", new ToolParameterType.Enum(new String[]{"One", "Two"}))
            ),
            Collections.emptyList()
        );

        ToolDescriptor actualDescriptor = SchemaGeneratorKt.getToolDescriptor(
            TypeToken.of(TestClass.class),
            toolName,
            toolDescription
        );

        assertEquals(expectedDescriptor, actualDescriptor);
    }
}
