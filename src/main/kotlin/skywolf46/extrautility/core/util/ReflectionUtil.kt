package skywolf46.extrautility.core.util

import io.github.classgraph.ClassGraph
import skywolf46.extrautility.core.abstraction.JvmFilter
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionProperties
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotations

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
}