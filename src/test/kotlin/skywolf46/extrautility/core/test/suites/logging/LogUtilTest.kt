package skywolf46.extrautility.core.test.suites.logging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import skywolf46.extrautility.core.test.data.TestSignalData
import skywolf46.extrautility.core.util.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class LogUtilTest {
    private val prefix = "ExtraUtility - Core/Test | "
    private val prefixClass = "ExtraUtility - Core/Log Test | "
    private val prefixSpecifiedClass = "ExtraUtility - Core/Log Test 2 | "

    @Order(0)
    @Test
    fun testNullLogPrefix() {
        assertNull(findPrefix())
    }

    @Test
    @Order(1)
    fun registerLogPrefix() {
        bindPrefix("skywolf46.extrautility.core.test.suites.logging", prefix)
    }

    @Test
    @Order(2)
    fun testLogPrefix() {
        assertEquals(prefix, findPrefix())
    }


    @Test
    @Order(3)
    fun testNonExistsPrefix() {
        assertNull(findPrefixFor("skywolf46.e"))
    }

    @Test
    @Order(3)
    fun testAppendedMessageWithEmptyString() {
        assertEquals(prefix, appendPrefix(""))
    }

    @Test
    @Order(4)
    fun testCurrentClassPrefix() {
        bindPrefix(prefixClass)
        assertEquals(prefixClass, findPrefix())
    }

    @Test
    @Order(5)
    fun testSpecifiedClassPrefix() {
        bindPrefixTo(TestSignalData::class, prefixSpecifiedClass)
        assertEquals(prefixSpecifiedClass, findPrefixFor(TestSignalData::class))
    }

    @Test
    @Order(6)
    fun testLogging() {
        log("Test logging")
    }

    @Test
    @Order(7)
    fun testLoggingError() {
        logError("Test error message")
    }
}
