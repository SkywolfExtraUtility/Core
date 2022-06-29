package skywolf46.extrautility.core.data

import skywolf46.extrautility.core.abstraction.Trigger

class UnregisterTrigger(private val unregister: () -> Unit) : Trigger {
    override fun run() {
        unregister()
    }
}