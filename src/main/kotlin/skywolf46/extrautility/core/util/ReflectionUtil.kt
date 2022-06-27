package skywolf46.extrautility.core.util

import io.github.classgraph.ClassGraph
import skywolf46.extrautility.core.abstraction.JvmFilter
import skywolf46.extrautility.core.definition.JvmModifier
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

object ReflectionUtil {
    val ignoreList = mutableListOf<String>()
    private val classLoaders = mutableListOf<ClassLoader>()
    private var classCache: ReflectionFilterContainer<KClass<*>>? = null
    private var methodCache: ReflectionFilterContainer<KFunction<*>>? = null
    private var fieldCache: ReflectionFilterContainer<KProperty<*>>? = null

    fun getClassCache(
        reloadClasses: Boolean = false,
        replaceClassLoader: Boolean = true
    ): ReflectionFilterContainer<KClass<*>> {
        if (classCache == null || reloadClasses) {
            classCache = filterClass(
                ClassGraph()
                    .apply {
                        if (replaceClassLoader)
                            overrideClassLoaders(*classLoaders.toTypedArray())
                        else
                            for (x in classLoaders)
                                addClassLoader(x)
                    }
                    .enableClassInfo()
                    .enableAnnotationInfo()
                    .scan()
                    .allClasses.loadClasses().map { it.kotlin })
        }
        return classCache!!
    }

    fun getMethodCache(
        reloadCache: Boolean = false,
        replaceClassLoader: Boolean = true,
        forceReloadClassCache: Boolean = false
    ): ReflectionFilterContainer<KFunction<*>> {
        if (methodCache == null || reloadCache) {
            methodCache = ReflectionFilterContainer(mutableListOf<KFunction<*>>().apply {
                getClassCache(forceReloadClassCache, replaceClassLoader).forEach {
                    this += it.declaredFunctions
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
            fieldCache = ReflectionFilterContainer(mutableListOf<KProperty<*>>().apply {
                getClassCache(forceReloadClassCache, replaceClassLoader).forEach {
                    this += it.declaredMemberProperties
                    this += it.declaredMemberExtensionProperties
                }
            })
        }
    }

    fun filterClass(classes: List<KClass<*>>): ReflectionFilterContainer<KClass<*>> {
        return ReflectionFilterContainer(classes)
    }

    fun filterFunction(properties: List<KFunction<*>>): ReflectionFilterContainer<KFunction<*>> {
        return ReflectionFilterContainer(properties)
    }

    fun filterField(properties: List<KProperty<*>>): ReflectionFilterContainer<KProperty<*>> {
        return ReflectionFilterContainer(properties)
    }

    class ReflectionFilterContainer<T : KAnnotatedElement>(private val data: List<T>) : Iterable<T> {
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

        fun requiresAny(vararg annotation: Class<Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    for (x in annotation)
                        if (it.findAnnotations(x.kotlin).isNotEmpty()) {
                            return@filter true
                        }
                    return@filter false
                }
            )
        }

        fun requires(vararg annotation: Class<Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    for (x in annotation)
                        if (it.findAnnotations(x.kotlin).isEmpty()) {
                            return@filter false
                        }
                    return@filter true
                }
            )
        }

        fun requiresNot(vararg annotation: Class<Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    for (x in annotation)
                        if (it.findAnnotations(x.kotlin).isNotEmpty()) {
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

    class CallableFunction<T>(val instance: Any?, val function: KFunction<*>) {
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

        fun invoke(vararg parameter: Any?) {
            // TODO
        }
    }
}

fun <T : Any> KFunction<T>.asCallable(instance: Any? = null): ReflectionUtil.CallableFunction<T> {
    if (instance == null) {
        return asSingletonCallable()
    }
    return ReflectionUtil.CallableFunction(instance, this)
}

internal fun <T : Any> KFunction<T>.asSingletonCallable(): ReflectionUtil.CallableFunction<T> {
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