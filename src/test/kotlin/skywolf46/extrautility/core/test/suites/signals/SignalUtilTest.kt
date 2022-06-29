package skywolf46.extrautility.core.test.suites.signals

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import skywolf46.extrautility.core.test.abstraction.CoreRequiredTest
import skywolf46.extrautility.core.test.data.*
import skywolf46.extrautility.core.test.exceptions.TestException
import skywolf46.extrautility.core.util.SignalUtil
import skywolf46.extrautility.core.util.asCallable
import skywolf46.extrautility.core.util.signal

@TestMethodOrder(OrderAnnotation::class)
class SignalUtilTest : CoreRequiredTest() {
    companion object {
        private const val NON_ORDERED = 0
        private const val FINALE = 1
    }

    @Test
    @Order(NON_ORDERED)
    fun testSignalListen() {
        assertTrue(SignalUtil.signal(TestSignalData()).isTestCompleted)
    }

    @Test
    @Order(NON_ORDERED)
    fun testForcePrioritySignalListen() {
        TestSignalData().signal(40).apply {
            assertFalse(isTestCompleted)
            assertTrue(isPriorityTestCompleted)
        }
    }

    @Test
    @Order(NON_ORDERED)
    fun testNonExistsPrioritySignalListen() {
        TestSignalData().signal(50).apply {
            assertFalse(isTestCompleted)
            assertFalse(isPriorityTestCompleted)
        }
    }

    @Test
    @Order(NON_ORDERED)
    fun testLambdaSignalReceiver() {
        SignalUtil.register(TestSignalData::class) {
            it.isLambdaTestCompleted = true
        }
        assertTrue(TestSignalData().signal().isLambdaTestCompleted)
    }

    @Test
    @Order(NON_ORDERED)
    fun testNoAnnotatedReceiverRegister() {
        SignalUtil.register(SignalUtilTest::class.java.getMethod("testNoAnnotatedReceiverRegister").asCallable(null))
            .apply {
                assertFalse(isSuccess)
            }
    }

    @Test
    @Order(NON_ORDERED)
    fun testLambdaPrioritySignalTest() {
        SignalUtil.register(TestSignalData::class, 20) {
            assertFalse(it.isLambdaPriorityTestCompleted)
        }
        SignalUtil.register(TestSignalData::class, 40) {
            it.isLambdaPriorityTestCompleted = true
        }
        SignalUtil.register(TestSignalData::class, 60) {
            assertTrue(it.isLambdaPriorityTestCompleted)
        }
        assertTrue(TestSignalData().signal().isLambdaPriorityTestCompleted)
        assertFalse(TestSignalData().signal(20).isTestCompleted)
    }

    @Test
    @Order(NON_ORDERED)
    fun testMultiplePriority() {
        TestSignalData().signal().apply {
            assertFalse(isMultiplePriorityTestCompleted)
            assertFalse(isSecondMultiplePriorityTestCompleted)
        }
        SignalUtil.register(TestSignalObjectSecond::class.java)
        TestSignalData().signal().apply {
            assertFalse(isMultiplePriorityTestCompleted)
            assertFalse(isSecondMultiplePriorityTestCompleted)
        }
        TestSignalData().signal(2).apply {
            assertTrue(isMultiplePriorityTestCompleted)
            assertTrue(isSecondMultiplePriorityTestCompleted)
        }
    }

    @Test
    @Order(NON_ORDERED)
    fun testInstanceReceiver() {
        assertFalse(TestSignalData().signal().isInstanceTestCompleted)
        assertFalse(TestSignalData().signal(2).isInstanceTestCompleted)
        SignalUtil.registerInstance(TestSignalClass())
        assertFalse(TestSignalData().signal().isInstanceTestCompleted)
        assertTrue(TestSignalData().signal(2).isInstanceTestCompleted)
    }

    @Test
    @Order(NON_ORDERED)
    fun testUnregisteringAll() {
        val unregister = SignalUtil.register(TestSignalObjectThird::class.java)
        assertThrows<TestException> {
            TestSignalData().signal()
        }
        assertThrows<TestException> {
            TestSignalData().signal(0, false)
        }
        assertFalse(
            TestSignalData().apply {
                try {
                    signal()
                } catch (e: Throwable) {
                    // Suppress test exception
                }
            }.isUnregisterTestCompleted
        )
        assertTrue(TestSignalData().signal(ignoreException = true).isUnregisterTestCompleted)
        assertTrue(TestSignalData().signal(2, true).isUnregisterTestCompleted)
        unregister.unregisterAll()
        assertFalse(TestSignalData().signal().isUnregisterTestCompleted)
    }

    @Test
    @Order(NON_ORDERED)
    fun testFailedReceiverUnregister() {
        SignalUtil.register(SignalUtilTest::class.java.getMethod("testNoAnnotatedReceiverRegister").asCallable(null))
            .apply {
                assertFalse(isSuccess)
                unregister()
            }
        SignalUtil.register(SignalUtilTest::class).unregisterAll()
    }

    @Test
    @Order(FINALE)
    fun testExceptions() {
        assertDoesNotThrow {
            TestSignalData().signal()
        }
        SignalUtil.register(TestSignalObjectFourth::class)
        assertThrows<TestException> {
            TestSignalData().signal()
        }
        assertThrows<TestException> {
            TestSignalData().signal(2)
        }
        assertDoesNotThrow {
            TestSignalData().signal(1)
        }
        assertDoesNotThrow {
            TestSignalData().signal(2, true)
        }
        assertDoesNotThrow {
            TestSignalData().signal(true)
        }
    }
}