package skywolf46.extrautility.core.test.suites.signals

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import skywolf46.extrautility.core.test.abstraction.CoreRequiredTest
import skywolf46.extrautility.core.test.data.TestSignalData
import skywolf46.extrautility.core.util.signal

class SignalUtilTest : CoreRequiredTest() {
    @Test
    fun testSignalListen() {
        assertTrue(TestSignalData().signal().isTestCompleted)
    }


    @Test
    fun testForcePrioritySignalListen() {
        TestSignalData().signal(40).apply {
            assertFalse(isTestCompleted)
            assertTrue(isPriorityTestCompleted)
        }
    }

    @Test
    fun testNonExistsPrioritySignalListen() {
        TestSignalData().signal(50).apply {
            assertFalse(isTestCompleted)
            assertFalse(isPriorityTestCompleted)
        }
    }
}