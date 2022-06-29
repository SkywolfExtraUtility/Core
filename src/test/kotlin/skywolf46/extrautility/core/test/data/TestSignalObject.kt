package skywolf46.extrautility.core.test.data

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import skywolf46.extrautility.core.annotations.RejectAutoRegister
import skywolf46.extrautility.core.annotations.SignalReceiver
import skywolf46.extrautility.core.test.exceptions.TestFailedException

object TestSignalObject {
    @SignalReceiver
    private fun TestSignalData.onSignal() {
        isTestCompleted = true
    }

    @SignalReceiver(priority = 20)
    private fun TestSignalData.onPrioritySignal() {
        assertFalse(isPriorityTestCompleted)
    }

    @SignalReceiver(priority = 40)
    private fun TestSignalData.onPrioritySignal2() {
        isPriorityTestCompleted = true
    }

    @SignalReceiver(priority = 80)
    private fun TestSignalData.onPrioritySignal3() {
        assertTrue(isPriorityTestCompleted)
    }

    @SignalReceiver
    private fun TestSignalData.onSignalButWillFailed(data: Any) {
        throw TestFailedException()
    }

    @SignalReceiver
    private fun onSignalButWillFailed() {
        throw TestFailedException()
    }

    @SignalReceiver
    @RejectAutoRegister
    private fun onSignalButRequiresRejected() {
        throw TestFailedException()
    }
}