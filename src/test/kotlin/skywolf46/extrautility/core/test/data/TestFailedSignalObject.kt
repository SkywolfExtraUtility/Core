package skywolf46.extrautility.core.test.data

import skywolf46.extrautility.core.annotations.RejectAutoRegister
import skywolf46.extrautility.core.annotations.SignalReceiver
import skywolf46.extrautility.core.test.exceptions.TestFailedException

@RejectAutoRegister
object TestFailedSignalObject {
    @SignalReceiver
    fun testFailingTestReceiver() {
        throw TestFailedException()
    }

    @RejectAutoRegister
    @SignalReceiver
    fun testFailingTestReceiverRejectingRegister() {
        throw TestFailedException()
    }
}