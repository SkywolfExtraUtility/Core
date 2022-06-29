package skywolf46.extrautility.core.util

import skywolf46.extrautility.core.annotations.RejectAutoRegister
import java.lang.reflect.Field
import java.lang.reflect.Method

@Suppress("MemberVisibilityCanBePrivate")
object AutoRegistrationUtil {
    private var classCache: ReflectionUtil.ReflectionFilterContainer<Class<*>>? = null

    private var methodCache: ReflectionUtil.ReflectionFilterContainer<Method>? = null

    private var fieldCache: ReflectionUtil.ReflectionFilterContainer<Field>? = null

    fun getClassCache(
        reloadClasses: Boolean = false,
        replaceClassLoader: Boolean = true
    ): ReflectionUtil.ReflectionFilterContainer<Class<*>> {
        if (classCache == null || reloadClasses) {
            classCache = ReflectionUtil.getClassCache(reloadClasses, replaceClassLoader)
                .requiresNot(RejectAutoRegister::class.java)
        }
        return classCache!!
    }

    fun getMethodCache(
        reloadClasses: Boolean = false,
        replaceClassLoader: Boolean = true
    ): ReflectionUtil.ReflectionFilterContainer<Method> {
        if (methodCache == null || reloadClasses) {
            methodCache =
                getClassCache(reloadClasses, replaceClassLoader)
                    .toMethodFilter()
                    .requiresNot(RejectAutoRegister::class.java)
        }
        return methodCache!!
    }

    fun getFieldCache(
        reloadClasses: Boolean = false,
        replaceClassLoader: Boolean = true
    ): ReflectionUtil.ReflectionFilterContainer<Field> {
        if (fieldCache == null || reloadClasses) {
            fieldCache =
                getClassCache(reloadClasses, replaceClassLoader)
                    .toFieldFilter()
                    .requiresNot(RejectAutoRegister::class.java)
        }
        return fieldCache!!
    }
}