package skywolf46.extrautility.core.util

import skywolf46.extrautility.core.annotations.SignalReceiver
import skywolf46.extrautility.core.enumeration.reflection.MethodFilter

object SignalUtil {
    init {
        bindPrefix("ExtraUtility - Core/Signal | ")
    }

    private val signals = mutableMapOf<Class<*>, SignalPriorityContainer>()

    @Suppress("UNCHECKED_CAST")
    internal fun init() {
        ReflectionUtil.getMethodCache()
            .filter(MethodFilter.INSTANCE_NOT_REQUIRED)
            .requiresAny(SignalReceiver::class.java)
            .unlock()
            .forEach {
                val callable = it.asCallable()
                if (callable.parameterCount() == 0) {
                    logError("Cannot register ${it.name} : No parameter found")
                    return@forEach
                }
                if (callable.parameterCount() != 1) {
                    logError("Cannot register ${it.name} : Signal listener cannot accept more than one parameter")
                    return@forEach
                }
                val annotation = it.getAnnotation(SignalReceiver::class.java)
                findContainer(callable.parameter()[0].type).register<Any>(annotation.priority, callable)
            }
    }

    fun findContainer(cls: Class<*>): SignalPriorityContainer {
        return signals.getOrPut(cls) { SignalPriorityContainer() }
    }

    fun signal(data: Any) {
        findContainer(data::class.java).onSignal(data)
    }

    fun signal(data: Any, forcedPriority: Int) {
        findContainer(data::class.java).onSignal(data, forcedPriority)
    }

    class SignalPriorityContainer {
        private val map = sortedMapOf<Int, MutableList<SignalReceiverContainer<Any>>>()

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> register(priority: Int, container: SignalReceiverContainer<T>) {
            map.getOrPut(priority) { mutableListOf() }
                .add(container as SignalReceiverContainer<Any>)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> register(priority: Int, caller: (T) -> Unit) {
            register(priority, SignalReceiverContainer(caller as (Any) -> Unit))
        }

        fun <T : Any> register(priority: Int, caller: ReflectionUtil.CallableFunction) {
            register(priority, FunctionSignalReceiverContainer<T>(caller))
        }

        fun onSignal(data: Any) {
            map.values.forEach { listeners ->
                listeners.forEach {
                    it.onSignal(data)
                }
            }
        }

        fun onSignal(data: Any, priority: Int) {
            map[priority]?.forEach {
                it.onSignal(data)
            }
        }
    }

    open class SignalReceiverContainer<T : Any>(private val processor: (T) -> Unit) {
        open fun onSignal(data: T) {
            processor(data)
        }
    }

    class FunctionSignalReceiverContainer<T : Any>(callable: ReflectionUtil.CallableFunction) :
        SignalReceiverContainer<T>({ callable.invoke(it) })
}

fun <T : Any> T.signal(): T {
    SignalUtil.signal(this)
    return this
}

fun <T : Any> T.signal(forcedPriority: Int): T {
    SignalUtil.signal(this, forcedPriority)
    return this
}