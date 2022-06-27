package skywolf46.extrautility.core.util

import kotlin.reflect.KClass

object InjectionUtil {
    fun <T : Any> instantiate(target: KClass<T>): T {

    }


}

inline fun <reified T : Any> instantiate(): T {
    return InjectionUtil.instantiate(T::class)
}