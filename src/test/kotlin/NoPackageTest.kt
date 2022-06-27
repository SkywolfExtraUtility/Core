import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import skywolf46.extrautility.core.util.LogUtil
import skywolf46.extrautility.core.util.appendPrefix
import skywolf46.extrautility.core.util.findPrefix

class NoPackageTest {
    @Test
    fun testNullPrefix() {
        assertNull(findPrefix())
    }


    @Test
    fun testNullPrefixMessage() {
        val testMessage = "Test prefix appende dmessage"
        assertEquals(testMessage, appendPrefix(testMessage))
    }
}