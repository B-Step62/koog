@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package ai.koog.serialization

import java.lang.reflect.Type
import kotlin.reflect.javaType

@Suppress("MissingKDocForPublicAPI")
public actual sealed interface TypeToken {
    /**
     * Factory functions to create [TypeToken]
     */
    public companion object {
        /**
         * Creates a [JavaTypeToken] from a Java [Type].
         *
         * Java usage:
         * ```java
         * TypeToken.of(MyClass.class);
         * ```
         */
        public fun of(type: Type): JavaTypeToken = JavaTypeToken(type)

        /**
         * Creates a [JavaTypeToken] from a [TypeCapture] anonymous subclass, preserving generic type information.
         *
         * Java usage:
         * ```java
         * // Non-generic
         * TypeToken.of(new TypeCapture<MyClass>() {});
         *
         * // Generic
         * TypeToken.of(new TypeCapture<List<String>>() {});
         * ```
         */
        @JvmStatic
        public fun of(capture: TypeCapture<*>): TypeToken {
            val superClass = capture.javaClass.genericSuperclass
            require(superClass is java.lang.reflect.ParameterizedType) {
                "TypeCapture must be parameterized. Use: new TypeCapture<YourType>() {}"
            }
            return JavaTypeToken(superClass.actualTypeArguments[0])
        }
    }
}

/**
 * Helper abstract class for capturing generic types from Java via anonymous subclass.
 */
public abstract class TypeCapture<@Suppress("unused") T>

/**
 * Java reflection-based type token implementation based on [Type].
 */
public class JavaTypeToken(
    public val type: Type,
) : TypeToken

/**
 * Converts this [TypeToken] to a [JavaTypeToken] exhaustively.
 */
@OptIn(ExperimentalStdlibApi::class)
public fun TypeToken.asJavaType(): JavaTypeToken = when (this) {
    is JavaTypeToken -> this
    is KotlinTypeToken -> JavaTypeToken(this.type.javaType)
}
