package skywolf46.extrautility.core.test.data

import skywolf46.extrautility.core.annotations.injection.AfterInitialize
import skywolf46.extrautility.core.annotations.injection.ExtraConfig

@ExtraConfig
class TestConfig(val test: TestExtraClass) {
    @AfterInitialize
    fun test() {
        println("Test1")
        test.isTestCompleted = true
    }
}

