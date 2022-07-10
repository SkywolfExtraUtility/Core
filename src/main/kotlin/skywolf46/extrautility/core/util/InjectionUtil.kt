package skywolf46.extrautility.core.util

import org.jetbrains.annotations.NotNull
import skywolf46.extrautility.core.annotations.injection.*
import skywolf46.extrautility.core.data.ArgumentStorage
import skywolf46.extrautility.core.enumeration.Stage
import skywolf46.extrautility.core.enumeration.reflection.ClassFilter
import skywolf46.extrautility.core.enumeration.reflection.MethodFilter
import skywolf46.extrautility.core.signals.pre.BeforeClassInjectSignal
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Parameter

object InjectionUtil {
    init {
        bindPrefix("ExtraUtility - Core/Injection | ")
    }

    private val classRegistration = mutableMapOf<Class<*>, ClassAnalysisData>()
    private val autoRegistration = mutableMapOf<Class<*>, Any>()

    internal fun init() {
        // Class instance will be added here
        BeforeClassInjectSignal().signal(ignoreException = true)
        // Injection classes load starts here.
        // Stage 1 - Loading extra classes
        initExtraClass()
        // Stage 2 - Loading extra config
        initExtraConfig()
        // Stage 3 - Loading extra module
        initExtraModule()
        // Stage 4 - Loading extra service
        initExtraService()
        // Stage 5 - Do late-init
        doLateInit()
        // Finalization - Invoke post-init methods
        doPostInit()
    }

    private fun initExtraClass() {
        log("Initializing ExtraClass..")
        ReflectionUtil.getClassCache().requires(ExtraClass::class.java).filter(ClassFilter.INSTANCE_REQUIRED)
            .apply {
                requiresAny(ExtraConfig::class.java, ExtraModule::class.java, ExtraService::class.java).forEach {
                    logError("ExtraClass registration denied for class ${it.name} : Auto registration class(Config, Module, Service) cannot initialize as ExtraClass")
                }
            }.requiresNot(ExtraConfig::class.java, ExtraModule::class.java, ExtraService::class.java).forEach {
                try {
                    log("Test: ${it.name}")
                    classRegistration[it] = ClassAnalysisData(it)
                } catch (e: Throwable) {
                    logError("ExtraClass registration denied for class ${it.name} : Unknown error occurred while registering ExtraClass")
                    e.printStackTrace()
                }
            }
    }

    private fun initExtraConfig() {
        log("Initializing ExtraConfig..")
        AutoRegistrationUtil.getClassCache()
            .requires(ExtraConfig::class.java)
            .unlock()
            .forEach {
                classRegistration[it] = ClassAnalysisData(it)
                autoRegistration[it] = classRegistration[it]!!.instantiate(Stage.PRE)
            }
    }

    private fun initExtraModule() {
        log("Initializing ExtraModule..")
        AutoRegistrationUtil.getClassCache()
            .requires(ExtraModule::class.java)
            .unlock()
            .forEach {
                classRegistration[it] = ClassAnalysisData(it)
                autoRegistration[it] = classRegistration[it]!!.instantiate(Stage.PRE)
            }
    }


    private fun initExtraService() {
        log("Initializing ExtraService..")
        AutoRegistrationUtil.getClassCache()
            .requires(ExtraService::class.java)
            .unlock()
            .forEach {
                classRegistration[it] = ClassAnalysisData(it)
                autoRegistration[it] = classRegistration[it]!!.instantiate(Stage.PRE)
            }
    }

    private fun doLateInit() {
        autoRegistration.values.forEach {
            classRegistration[it.javaClass]!!.doLateInit(it)
        }
    }

    private fun doPostInit() {
        autoRegistration.values.forEach {
            println(it)
            classRegistration[it.javaClass]!!.invokeInitializer(Stage.POST, it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> instantiate(target: Class<T>): T? {
        autoRegistration[target]?.apply {
            return this as T
        }
        return classRegistration[target]?.instantiate(Stage.POST) as T?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> instantiateWith(target: Class<T>, args: ArgumentStorage): T? {
        autoRegistration[target]?.apply {
            return this as T
        }
        return classRegistration[target]?.instantiateWith(Stage.POST, args) as T?
    }

    fun <T : Any> get(target: Class<T>): T {
        return instantiate(target)!!
    }


    private class ClassAnalysisData(cls: Class<*>) {
        private val constructor: ReflectionUtil.CallableFunction
        private val constructorRequirements: List<ConstructorParameter>
        private val lateInitFields: Map<Field, Class<*>>
        private val injectFields: Map<Field, Class<*>>
        private val initializingFunction: List<Method>
        private val postInitializingFunction: List<Method>

        init {
            if (cls.constructors.isEmpty() || cls.constructors.size > 1) {
                throw IllegalStateException("Cannot register class ${cls.name} : Class constructor count must have to 1")
            }
            constructorRequirements = cls.constructors[0].parameters.map { x ->
                ConstructorParameter(x)
            }
            ReflectionUtil.filterField(cls.declaredFields.toList()).requires(Inject::class.java).unlock().apply {
                injectFields = requiresNot(LateInit::class.java).associateWith { it.type }
                lateInitFields = requiresAny(LateInit::class.java).associateWith { it.type }
            }
            constructor = cls.constructors[0].asCallable()
            ReflectionUtil.filterMethod(cls.declaredMethods.toList())
                .requires(AfterInitialize::class.java)
                .filter(MethodFilter.INSTANCE_REQUIRED)
                .unlock()
                .apply {
                    filter { it.parameterCount != 0 }.forEach {
                        logError("")
                    }
                }.filter { it.parameterCount == 0 }.apply {
                    initializingFunction =
                        filter { x -> x.getAnnotation(AfterInitialize::class.java).stage == Stage.PRE }
                    postInitializingFunction =
                        filter { x -> x.getAnnotation(AfterInitialize::class.java).stage == Stage.POST }
                }
        }

        fun instantiate(initStage: Stage?): Any {
            val parameter = constructorRequirements.map { instantiate(it.cls) }
            val data = constructor.invoke(parameter)!!
            injectFields.forEach { (k, v) ->
                k.set(data, instantiate(v))
            }
            if (initStage != null)
                invokeInitializer(initStage, data)
            return data
        }

        fun instantiateWith(initStage: Stage?, args: ArgumentStorage): Any {
            val parameter = constructorRequirements.map { args[it.cls] ?: instantiate(it.cls) }
            val data = constructor.invoke(parameter)!!
            injectFields.forEach { (k, v) ->
                k.set(data, instantiate(v))
            }
            if (initStage != null)
                invokeInitializer(initStage, data)
            return data
        }

        fun invokeInitializer(stage: Stage, data: Any) {
            if (stage == Stage.PRE) {
                for (x in initializingFunction) {
                    x.invoke(data)
                }
            } else {
                for (x in postInitializingFunction) {
                    x.invoke(data)
                }
            }
        }

        fun doLateInit(data: Any) {
            lateInitFields.forEach {
                instantiate(it.value)?.apply {
                    it.key.set(data, this)
                }
            }
        }
    }


    private class ConstructorParameter(parameter: Parameter) {
        val cls: Class<*> = parameter.type
        val isNullable: Boolean = parameter.getAnnotation(NotNull::class.java) != null
    }

}

inline fun <reified T : Any> instantiate(): T {
    return InjectionUtil.instantiate(T::class.java) as T
}

inline fun <reified T : Any> instantiateWith(args: ArgumentStorage): T {
    return InjectionUtil.instantiateWith(T::class.java, args) as T
}