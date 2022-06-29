package skywolf46.extrautility.core.test.abstraction

import skywolf46.extrautility.core.ExtraUtilityCore

abstract class CoreRequiredTest {
    companion object {
        var initialized = false
    }

    init {
        if (!initialized) {
            initialized = true
            ExtraUtilityCore.init()
        }
    }
}