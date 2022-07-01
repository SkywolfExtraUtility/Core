package skywolf46.extrautility.core

import skywolf46.extrautility.core.util.CharacterUtil
import skywolf46.extrautility.core.util.InjectionUtil
import skywolf46.extrautility.core.util.SignalUtil

object ExtraUtilityCore {
    private var initialized = false
    fun init() {
        if (initialized) {
            throw IllegalStateException("Double-initialization detected")
        }
        initialized = true
        CharacterUtil.init()
        SignalUtil.init()
        InjectionUtil.init()
    }
}