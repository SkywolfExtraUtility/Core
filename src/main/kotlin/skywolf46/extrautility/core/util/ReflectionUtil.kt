package skywolf46.extrautility.core.util

import io.github.classgraph.ClassGraph
import skywolf46.extrautility.core.abstraction.JvmFilter
import skywolf46.extrautility.core.definition.JvmModifier
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.*
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.jvm.*

object ReflectionUtil {
    val ignoreList = mutableListOf<String>()
    private val classLoaders = mutableListOf<ClassLoader>()
    private var classCache: ReflectionFilterContainer<Class<*>>? = null
    private var methodCache: ReflectionFilterContainer<Method>? = null
    private var fieldCache: ReflectionFilterContainer<Field>? = null

    fun getClassCache(
        reloadClasses: Boolean = false,
        replaceClassLoader: Boolean = true
    ): ReflectionFilterContainer<Class<*>> {
        if (classCache == null || reloadClasses) {
            classCache = filterClass(
                ClassGraph()
                    .apply {
                        if (replaceClassLoader)
                            overrideClassLoaders(
                                *classLoaders.toMutableList().append(javaClass.classLoader).toTypedArray()
                            )
                        else
                            for (x in classLoaders)
                                addClassLoader(x)
                    }
                    .enableClassInfo()
                    .enableAnnotationInfo()
                    .scan()
                    .allClasses.loadClasses())
        }
        return classCache!!
    }

    fun getMethodCache(
        reloadCache: Boolean = false,
        replaceClassLoader: Boolean = true,
        forceReloadClassCache: Boolean = false
    ): ReflectionFilterContainer<Method> {
        if (methodCache == null || reloadCache) {
            methodCache = ReflectionFilterContainer(mutableListOf<Method>().apply {
                getClassCache(forceReloadClassCache, replaceClassLoader).forEach { cls ->
                    cls.declaredMethods.forEach {
                        if (it != null)
                            this += it
                    }
                }
            })
        }
        return methodCache!!
    }

    fun getFieldCache(
        reloadCache: Boolean = false,
        replaceClassLoader: Boolean = true,
        forceReloadClassCache: Boolean = false
    ) {
        if (fieldCache == null || reloadCache) {
            fieldCache = ReflectionFilterContainer(mutableListOf<Field>().apply {
                getClassCache(forceReloadClassCache, replaceClassLoader).forEach {
                    this += it.declaredFields
                }
            })
        }
    }

    fun filterClass(classes: List<Class<*>>): ReflectionFilterContainer<Class<*>> {
        return ReflectionFilterContainer(classes)
    }

    fun filterFunction(properties: List<Method>): ReflectionFilterContainer<Method> {
        return ReflectionFilterContainer(properties)
    }

    fun filterField(properties: List<Field>): ReflectionFilterContainer<Field> {
        return ReflectionFilterContainer(properties)
    }

    fun findParentClasses(cls: Class<out Any>): List<Class<out Any>> {
        val classes = mutableListOf<Class<out Any>>()
        var clsOrig: Class<*>? = cls
        do {
            if (clsOrig == null)
                return classes.distinct()
            classes += clsOrig
            for (x in clsOrig.interfaces) {
                classes += findParentClasses(x)
            }
            clsOrig = clsOrig.superclass
        } while (clsOrig != Any::class.java)
        return classes.distinct()
    }

    class ReflectionFilterContainer<T : AnnotatedElement>(private val data: List<T>) : Iterable<T> {
        fun unlock(): ReflectionFilterContainer<T> {
            if (data.isEmpty() || data[0] !is KCallable<*>)
                return this
            data.forEach {
                (it as KCallable<*>).isAccessible = true
            }
            return this
        }

        fun filter(filter: JvmFilter<T>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter { filter.isSatisfied(it) }
            )
        }

        fun requiresAny(vararg annotation: Class<out Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    for (x in annotation)
                        if (it.getAnnotationsByType(x).isNotEmpty()) {
                            return@filter true
                        }
                    return@filter false
                }
            )
        }

        fun requires(vararg annotation: Class<out Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    for (x in annotation)
                        if (it.getAnnotationsByType(x).isEmpty()) {
                            return@filter false
                        }
                    return@filter true
                }
            )
        }

        fun requiresNot(vararg annotation: Class<out Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    for (x in annotation)
                        if (it.getAnnotationsByType(x).isNotEmpty()) {
                            return@filter false
                        }
                    return@filter true
                }
            )
        }

        override fun iterator(): Iterator<T> {
            return data.iterator()
        }
    }

    open class CallableFunction(val instance: Any?, val function: KFunction<*>) {
        fun parameter(): List<KParameter> {
            return function.parameters
        }

        fun parameterCount(): Int {
            return parameter().size
        }

        fun doReturn(cls: KClass<*>): Boolean {
            return cls.isSuperclassOf(function.returnType.jvmErasure)
        }

        fun doAccept(vararg cls: KClass<*>): Boolean {
            val parameters = parameter()
            return parameters.size == cls.size
                    && cls.allIndexed { cls[it].isSuperclassOf(parameters[it].type.jvmErasure) }
        }

        open fun invoke(vararg parameter: Any?) {
            function.call(*parameter)
        }

        fun asAutoMatchingFunction(): AutoMatchedCallableFunction {
            return AutoMatchedCallableFunction(instance, function)
        }
    }

    class AutoMatchedCallableFunction(instance: Any?, function: KFunction<*>) : CallableFunction(instance, function) {
        private val matched = mutableMapOf<String, KClass<*>>()
        private val strictMatched = mutableMapOf<String, KClass<*>>()

        private class FieldProperty(val type: KClass<*>) {
            // TODO
        }
    }
}

fun <T : Any> KFunction<T>.asCallable(instance: Any? = null): ReflectionUtil.CallableFunction {
    if (instance == null) {
        return asSingletonCallable()
    }
    return ReflectionUtil.CallableFunction(instance, this)
}

internal fun <T : Any> KFunction<T>.asSingletonCallable(): ReflectionUtil.CallableFunction {
    if (JvmModifier.isStatic((javaMethod ?: javaConstructor)!!.modifiers)) {
        return ReflectionUtil.CallableFunction(null, this)
    }
    val declaringClass = (javaMethod ?: javaConstructor)!!.declaringClass.kotlin
    if (declaringClass.isCompanion) {
        return ReflectionUtil.CallableFunction(declaringClass.companionObject, this)
    }
    declaringClass.objectInstance?.let { instance ->
        return ReflectionUtil.CallableFunction(instance, this)
    }
    throw IllegalStateException("Cannot convert instance required function to singleton callable")
}

fun <T : Any> Class<T>.findParentClasses(): List<Class<out Any>> {
    return ReflectionUtil.findParentClasses(this)
}


fun <T : Any> KClass<T>.findParentClasses(): List<KClass<out Any>> {
    return java.findParentClasses().map { x -> x.kotlin }
}

fun Class<*>.safeKotlin(): KClass<*>? {
    return try {
        kotlin
    } catch (e: Throwable) {
        null
    }
}

fun Method.safeKotlin(): KFunction<*>? {
    return try {
        kotlinFunction
    } catch (e: Throwable) {
        null
    }
}

fun Field.safeKotlin(): KProperty<*>? {
    return try {
        kotlinProperty
    } catch (e: Throwable) {
        null
    }
}