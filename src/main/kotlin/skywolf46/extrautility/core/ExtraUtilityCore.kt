package skywolf46.extrautility.core

import skywolf46.extrautility.core.util.CharacterUtil
import skywolf46.extrautility.core.util.SignalUtil
import java.io.File

object ExtraUtilityCore {
    private var initialized = false
    fun init() {
        if (initialized) {
            throw IllegalStateException("Double-initialization detected")
        }
        initialized = true
        CharacterUtil.init()
        SignalUtil.init()
    }

    fun applyAddon(jar: File) {

    }
}