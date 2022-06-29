package skywolf46.extrautility.core.test.data

import skywolf46.extrautility.core.annotations.RejectAutoRegister
import skywolf46.extrautility.core.annotations.SignalReceiver
import skywolf46.extrautility.core.test.exceptions.TestException
import skywolf46.extrautility.core.test.exceptions.TestFailedException

object TestSignalObjectThird {
    @RejectAutoRegister
    @SignalReceiver(0)
    fun TestSignalData.testFailingTestReceiver() {
        throw TestException()
    }

    @RejectAutoRegister
    @SignalReceiver(1)
    fun TestSignalData.testDroppingExceptionTestReceiver() {
        throw TestFailedException()
    }

    @RejectAutoRegister
    @SignalReceiver(2)
    fun TestSignalData.testNonExceptionTestReceiver() {
        isUnregisterTestCompleted = true
    }

    @RejectAutoRegister
    @SignalReceiver(3)
    fun TestSignalData.testMultiplePriorityThrow() {
        throw TestException()
    }


    @RejectAutoRegister
    @SignalReceiver(3)
    fun TestSignalData.testMultiplePriority() {
        isMultiplePriorityTestCompleted = true
    }
}