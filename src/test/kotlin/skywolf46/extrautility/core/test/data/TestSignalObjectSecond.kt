package skywolf46.extrautility.core.test.data

import skywolf46.extrautility.core.annotations.RejectAutoRegister
import skywolf46.extrautility.core.annotations.SignalReceiver

object TestSignalObjectSecond {
    @RejectAutoRegister
    @SignalReceiver(1)
    fun TestSignalData.multiplePriorityTestPreCleanup() {
        isMultiplePriorityTestCompleted = false
        isSecondMultiplePriorityTestCompleted = false
    }

    @RejectAutoRegister
    @SignalReceiver(2)
    fun TestSignalData.testMultiplePriorityThrow() {
        isMultiplePriorityTestCompleted = true
    }


    @RejectAutoRegister
    @SignalReceiver(2)
    fun TestSignalData.testMultiplePriority() {
        isSecondMultiplePriorityTestCompleted = true
    }

    @RejectAutoRegister
    @SignalReceiver(3)
    fun TestSignalData.multiplePriorityPostCleanup() {
        isMultiplePriorityTestCompleted = false
        isSecondMultiplePriorityTestCompleted = false
    }
}