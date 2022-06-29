package skywolf46.extrautility.core.test.data

import skywolf46.extrautility.core.annotations.SignalReceiver

class TestSignalClass {
    @SignalReceiver(1)
    fun TestSignalData.onPreTest() {
        isInstanceTestCompleted = false
    }

    @SignalReceiver(2)
    fun TestSignalData.onTest() {
        isInstanceTestCompleted = true
    }


    @SignalReceiver(3)
    fun TestSignalData.onPostTest() {
        isInstanceTestCompleted = false
    }
}
