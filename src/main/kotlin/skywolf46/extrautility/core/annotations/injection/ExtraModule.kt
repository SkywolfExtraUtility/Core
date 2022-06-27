package skywolf46.extrautility.core.annotations.injection

/**
 * Declare class as SkywolfExtraUtility module class.
 *  Module class is backend processor logic stage.
 *
 * Module class are highly not recommended for self-instantiation,
 *  and will be auto initialized after SkywolfExtraUtilty Core initialization.
 * Class initialization will proceed in the order of [ExtraConfig] > [ExtraModule] > [ExtraService].
 *
 * Module class must have public constructor to inject, but cannot inject [ExtraService] on constructor injection.
 *  Reversed order of object injection is not recommended, but if it required, it can inject with [LateInit] annotated field.
 *  [LateInit] annotated mutable field will inject after Core initialized, and will not occur circled dependency problem.
 *
 * Circled dependency of auto-injection class will throw an exception, and related class will be unregistered.
 *   Library developer must have to check dependency of classes to avoid system breakdown.
 *
 * Automatic field injection instance can be added on [skywolf46.extrautility.core.signals.pre.BeforeClassInjectSignal] signal.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtraModule