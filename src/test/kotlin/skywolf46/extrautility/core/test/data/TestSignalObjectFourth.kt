package skywolf46.extrautility.core.test.data

import skywolf46.extrautility.core.annotations.RejectAutoRegister
import skywolf46.extrautility.core.annotations.SignalReceiver
import skywolf46.extrautility.core.test.exceptions.TestException

@RejectAutoRegister
object TestSignalObjectFourth {
    @SignalReceiver(1)
    fun TestSignalData.testNothing() {
        // Do nothing
    }

    @SignalReceiver(2)
    fun TestSignalData.testThrow() {
        throw TestException()
    }
}