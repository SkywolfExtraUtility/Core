package skywolf46.extrautility.core.util

import io.github.classgraph.ClassGraph
import skywolf46.extrautility.core.abstraction.JvmFilter
import skywolf46.extrautility.core.data.ArgumentStorage
import skywolf46.extrautility.core.definition.JvmModifier
import java.lang.reflect.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.jvm.kotlinProperty

object ReflectionUtil {
    val ignoreList = mutableListOf<String>()
    private val classLoaders = mutableListOf<ClassLoader>()
    private var classCache: ReflectionFilterContainer<Class<*>>? = null
    private var methodCache: ReflectionFilterContainer<Method>? = null
    private var fieldCache: ReflectionFilterContainer<Field>? = null

    fun addClassLoader(loader: ClassLoader) {
        classLoaders += loader
    }

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
            methodCache =
                ReflectionFilterContainer(getClassCache(forceReloadClassCache, replaceClassLoader).flatMap { cls ->
                    try {
                        cls.declaredMethods.toList()
                    } catch (e: Throwable) {
                        // Ignore if failed to get methods
                        emptyList()
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
            fieldCache =
                ReflectionFilterContainer(getClassCache(forceReloadClassCache, replaceClassLoader).flatMap { cls ->
                    try {
                        cls.declaredFields.toList()
                    } catch (e: Throwable) {
                        // Ignore if failed to get methods
                        emptyList()
                    }
                })
        }
    }

    fun filterClass(classes: List<Class<*>>): ReflectionFilterContainer<Class<*>> {
        return ReflectionFilterContainer(classes)
    }

    fun filterMethod(properties: List<Method>): ReflectionFilterContainer<Method> {
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
            if (data.isNotEmpty() && data[0] is AccessibleObject) {
                data.forEach {
                    (it as AccessibleObject).isAccessible = true
                }
            }
            return this
        }

        fun filter(filter: JvmFilter<T>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter { filter.isSatisfied(it) }
            )
        }

        fun filterNot(filter: JvmFilter<T>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter { !filter.isSatisfied(it) }
            )
        }

        fun requiresAny(vararg annotation: Class<out Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    return@filter annotation.any { annotation -> it.getAnnotationsByType(annotation).isNotEmpty() }
                }
            )
        }

        fun requires(vararg annotation: Class<out Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    return@filter annotation.none { annotation -> it.getAnnotationsByType(annotation).isEmpty() }
                }
            )
        }

        fun requiresNot(vararg annotation: Class<out Annotation>): ReflectionFilterContainer<T> {
            return ReflectionFilterContainer(
                data.filter {
                    return@filter annotation.none { annotation -> it.getAnnotationsByType(annotation).isNotEmpty() }
                }
            )
        }

        override fun iterator(): Iterator<T> {
            return data.iterator()
        }
    }

    abstract class FunctionExecutor {
        private var cachedParameter: List<Parameter>? = null

        private var cachedReturnType: Class<*>? = null

        internal abstract fun acquireParameter(): List<Parameter>

        internal abstract fun acquireReturnType(): Class<*>

        abstract fun getDeclaringClassName(): String

        abstract fun getFunctionName(): String

        abstract fun getFullName(): String

        protected abstract fun execute(args: List<Any?>): Any?

        abstract fun <T : Annotation> findAnnotation(type: Class<T>): T?


        fun invoke(args: List<Any?>): Any? {
            try {
                return execute(args)
            } catch (e: InvocationTargetException) {
                throw e.cause ?: e
            }
        }

        fun parameter(): List<Parameter> {
            if (cachedParameter == null) {
                cachedParameter = acquireParameter()
            }
            return cachedParameter!!.toList()
        }

        fun returnType(): Class<*> {
            if (cachedReturnType == null) {
                cachedReturnType = acquireReturnType()
            }
            return cachedReturnType!!
        }

        fun parameterCount(): Int {
            return parameter().size
        }

    }

    internal class DefaultFunctionExecutor(private val instance: Any?, private val function: Method) :
        FunctionExecutor() {
        override fun acquireParameter(): List<Parameter> {
            return function.parameters.toList()
        }

        override fun acquireReturnType(): Class<*> {
            return function.returnType
        }

        override fun getDeclaringClassName(): String {
            return function.declaringClass.name
        }

        override fun getFunctionName(): String {
            return function.name
        }

        override fun getFullName(): String {
            return "${getDeclaringClassName()}#${getFunctionName()}"
        }

        override fun execute(args: List<Any?>): Any? {
            return function.invoke(instance, *args.toTypedArray())
        }

        override fun <T : Annotation> findAnnotation(type: Class<T>): T? {
            return function.getAnnotation(type)
        }
    }

    internal class ConstructorExecutor(private val constructor: Constructor<*>) :
        FunctionExecutor() {
        override fun acquireParameter(): List<Parameter> {
            return constructor.parameters.toList()
        }

        override fun acquireReturnType(): Class<*> {
            return constructor.declaringClass
        }

        override fun getDeclaringClassName(): String {
            return constructor.declaringClass.name
        }

        override fun getFunctionName(): String {
            return "${constructor.declaringClass.name}()"
        }

        override fun getFullName(): String {
            return getFunctionName()
        }

        override fun execute(args: List<Any?>): Any? {
            return constructor.newInstance(*args.toTypedArray())
        }

        override fun <T : Annotation> findAnnotation(type: Class<T>): T? {
            return constructor.getAnnotation(type)
        }
    }

    open class CallableFunction(private val executor: FunctionExecutor) : FunctionExecutor() {
        override fun acquireParameter(): List<Parameter> {
            return executor.parameter()
        }

        override fun acquireReturnType(): Class<*> {
            return executor.returnType()
        }

        override fun getDeclaringClassName(): String {
            return executor.getDeclaringClassName()
        }

        override fun getFunctionName(): String {
            return executor.getFunctionName()
        }

        override fun getFullName(): String {
            return executor.getFullName()
        }

        override fun execute(args: List<Any?>): Any? {
            return executor.invoke(args)
        }

        override fun <T : Annotation> findAnnotation(type: Class<T>): T? {
            return executor.findAnnotation(type)
        }

        fun <T : Annotation> findAnnotation(kls: KClass<T>): T? {
            return findAnnotation(kls.java)
        }

        inline fun <reified T : Annotation> getAnnotation(): T? {
            return findAnnotation(T::class)
        }

        fun asAutoMatchingFunction(): AutoMatchedCallableFunction {
            return AutoMatchedCallableFunction(executor)
        }

        open fun doReturn(cls: Class<*>): Boolean {
            return cls.isAssignableFrom(executor.returnType())
        }

        open fun doAccept(vararg cls: Class<*>): Boolean {
            val parameters = executor.parameter()
            return parameters.size == cls.size
                    && cls.allIndexed { parameters[it].type.isAssignableFrom(cls[it]) }
        }

    }


    class AutoMatchedCallableFunction(executor: FunctionExecutor) : CallableFunction(executor) {
        private val matched = mutableMapOf<Int, Class<*>>()

        // TODO
        private val strictMatched = mutableMapOf<Int, Pair<String, Class<*>>>()

        init {
            for (x in 0 until parameterCount()) {
                matched[x] = executor.parameter()[x].type
            }
        }

        fun execute(args: ArgumentStorage): Any? {
            val parameters = mutableListOf<Any?>()
            for (x in 0 until parameterCount()) {
                parameters += null
            }
            val classCounter = mutableMapOf<Class<*>, Int>()
            matched.forEach { (k, v) ->
                classCounter[v] = classCounter.getOrElse(v) { 0 } + 1
                parameters[k] = args.getAll(v).getOrNull(classCounter[v]!! - 1)
            }
            return invoke(parameters)
        }
    }
}

fun <T : Class<*>> ReflectionUtil.ReflectionFilterContainer<T>.toMethodFilter(): ReflectionUtil.ReflectionFilterContainer<Method> {
    return ReflectionUtil.filterMethod(flatMap<T, Method> {
        kotlin.runCatching { it.declaredMethods.toList() }.getOrElse { emptyList() }
    })
}

fun <T : Class<*>> ReflectionUtil.ReflectionFilterContainer<T>.toFieldFilter(): ReflectionUtil.ReflectionFilterContainer<Field> {
    return ReflectionUtil.filterField(flatMap<T, Field> {
        kotlin.runCatching { it.declaredFields.toList() }.getOrElse { emptyList() }
    })
}

fun Constructor<*>.asCallable(): ReflectionUtil.CallableFunction {
    return ReflectionUtil.CallableFunction(ReflectionUtil.ConstructorExecutor(this))
}

fun Method.asCallable(instance: Any? = null): ReflectionUtil.CallableFunction {
    return ReflectionUtil.CallableFunction(ReflectionUtil.DefaultFunctionExecutor(instance, this))
}

fun Method.asSingletonCallable(): ReflectionUtil.CallableFunction {
    if (JvmModifier.isStatic(modifiers)) {
        return ReflectionUtil.CallableFunction(ReflectionUtil.DefaultFunctionExecutor(null, this))
    }
    declaringClass.safeKotlin()?.apply {
        if (isCompanion) {
            return ReflectionUtil.CallableFunction(
                ReflectionUtil.DefaultFunctionExecutor(
                    companionObject,
                    this@asSingletonCallable
                )
            )
        }
        objectInstance?.let { instance ->
            return ReflectionUtil.CallableFunction(
                ReflectionUtil.DefaultFunctionExecutor(
                    instance,
                    this@asSingletonCallable
                )
            )
        }
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