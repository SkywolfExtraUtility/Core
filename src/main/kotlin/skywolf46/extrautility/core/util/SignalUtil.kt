package skywolf46.extrautility.core.util

import skywolf46.extrautility.core.annotations.SignalReceiver
import skywolf46.extrautility.core.data.UnregisterTrigger
import skywolf46.extrautility.core.enumeration.reflection.MethodFilter
import java.lang.reflect.Method
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
object SignalUtil {
    init {
        bindPrefix("ExtraUtility - Core/Signal | ")
    }

    private val signals = mutableMapOf<Class<*>, SignalPriorityContainer>()

    @Suppress("UNCHECKED_CAST")
    internal fun init() {
        AutoRegistrationUtil.getMethodCache()
            .filter(MethodFilter.INSTANCE_NOT_REQUIRED)
            .requiresAny(SignalReceiver::class.java)
            .unlock()
            .forEach {
                register(it.asSingletonCallable()).message?.apply {
                    logError(this)
                }
            }
    }

    fun findContainer(cls: Class<*>): SignalPriorityContainer {
        return signals.getOrPut(cls) { SignalPriorityContainer() }
    }

    fun <T : Any> signal(data: T): T {
        return signal(data, false)
    }

    fun <T : Any> signal(data: T, ignoreException: Boolean): T {
        return findContainer(data::class.java).onSignal(data, ignoreException)
    }

    fun <T : Any> signal(data: T, forcedPriority: Int, ignoreException: Boolean): T {
        return findContainer(data::class.java).onSignal(data, forcedPriority, ignoreException)
    }

    fun register(
        callable: ReflectionUtil.CallableFunction
    ): SignalReceiverResult {
        val annotation = callable.findAnnotation(SignalReceiver::class.java)
            ?: return SignalReceiverResult(
                false,
                "Cannot register ${callable.getFullName()} : Method receiver requires @SignalReceiver annotation"
            )
        if (callable.parameterCount() == 0) {
            return SignalReceiverResult(false, "Cannot register ${callable.getFullName()} : No parameter found")
        }
        if (callable.parameterCount() != 1) {
            return SignalReceiverResult(
                false,
                "Cannot register ${callable.getFullName()} : Signal listener cannot accept more than one parameter"
            )
        }
        val trigger = findContainer(callable.parameter()[0].type).register<Any>(annotation.priority, callable)
        return SignalReceiverResult(true, trigger = trigger)
    }

    fun <T : Any> register(signal: Class<T>, priority: Int, unit: (T) -> Unit) {
        findContainer(signal).register(priority, unit)
    }

    fun <T : Any> register(signal: Class<T>, unit: (T) -> Unit) {
        register(signal, 0, unit)
    }

    fun <T : Any> register(signal: KClass<T>, priority: Int, unit: (T) -> Unit) {
        register(signal.java, priority, unit)
    }

    fun <T : Any> register(signal: KClass<T>, unit: (T) -> Unit) {
        register(signal.java, unit)
    }

    fun register(target: Class<*>): SignalReceiverResultContainer {
        return ReflectionUtil.filterMethod(target.methods.toList())
            .filter(MethodFilter.INSTANCE_NOT_REQUIRED)
            .requiresAny(SignalReceiver::class.java)
            .unlock()
            .associateWith {
                register(it.asSingletonCallable())
            }
            .let(::SignalReceiverResultContainer)
    }

    fun register(target: KClass<*>): SignalReceiverResultContainer {
        return register(target.java)
    }

    fun registerInstance(instance: Any): SignalReceiverResultContainer {
        return ReflectionUtil.filterMethod(instance::class.java.methods.toList())
            .filter(MethodFilter.INSTANCE_REQUIRED)
            .requiresAny(SignalReceiver::class.java)
            .unlock()
            .associateWith {
                register(it.asCallable(instance))
            }
            .let(::SignalReceiverResultContainer)
    }

    data class SignalReceiverResult(
        val isSuccess: Boolean,
        val message: String? = null,
        private val trigger: UnregisterTrigger? = null
    ) {
        fun unregister() {
            trigger?.invoke()
        }
    }

    data class SignalReceiverResultContainer(val result: Map<Method, SignalReceiverResult>) {
        fun unregisterAll() {
            result.values.forEach {
                it.unregister()
            }
        }
    }

    class SignalPriorityContainer {
        private val map = sortedMapOf<Int, MutableList<SignalReceiverContainer<Any>>>()

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> register(priority: Int, container: SignalReceiverContainer<T>): UnregisterTrigger {
            map.getOrPut(priority) { mutableListOf() }
                .add(container as SignalReceiverContainer<Any>)
            return UnregisterTrigger {
                map[priority]!!.remove(container)
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> register(priority: Int, caller: (T) -> Unit): UnregisterTrigger {
            return register(priority, SignalReceiverContainer(caller as (Any) -> Unit))
        }

        fun <T : Any> register(priority: Int, caller: ReflectionUtil.CallableFunction): UnregisterTrigger {
            return register(priority, FunctionSignalReceiverContainer<T>(caller))
        }

        fun <T : Any> onSignal(data: T, ignoreException: Boolean): T {
            map.values.forEach { listeners ->
                listeners.forEach {
                    if (ignoreException) {
                        try {
                            it.onSignal(data)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    } else {
                        it.onSignal(data)
                    }
                }
            }
            return data
        }

        fun <T : Any> onSignal(data: T, priority: Int, ignoreException: Boolean): T {
            map[priority]?.forEach {
                if (ignoreException) {
                    try {
                        it.onSignal(data)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                } else {
                    it.onSignal(data)
                }
            }
            return data
        }
    }

    open class SignalReceiverContainer<T : Any>(private val processor: (T) -> Unit) {
        open fun onSignal(data: T) {
            processor(data)
        }
    }

    class FunctionSignalReceiverContainer<T : Any>(callable: ReflectionUtil.CallableFunction) :
        SignalReceiverContainer<T>({ callable.invoke(mutableListOf(it)) })
}

fun <T : Any> T.signal(ignoreException: Boolean = false): T {
    SignalUtil.signal(this, ignoreException)
    return this
}

fun <T : Any> T.signal(forcedPriority: Int, ignoreException: Boolean = false): T {
    SignalUtil.signal(this, forcedPriority, ignoreException)
    return this
}