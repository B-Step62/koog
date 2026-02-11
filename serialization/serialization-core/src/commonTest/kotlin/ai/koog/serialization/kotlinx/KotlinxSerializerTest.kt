package ai.koog.serialization.kotlinx

import ai.koog.serialization.KoogSerializerTestBase
import kotlinx.serialization.json.Json
import kotlin.test.Test

/**
 * General [KotlinxSerializer] test
 */
class KotlinxSerializerTest : KoogSerializerTestBase() {
    override val serializer = KotlinxSerializer(Json)

    @Test
    override fun testSerializeDeserialize() {
        super.testSerializeDeserialize()
    }

    @Test
    override fun testSerializeDeserializeJSONElement() {
        super.testSerializeDeserializeJSONElement()
    }
}
