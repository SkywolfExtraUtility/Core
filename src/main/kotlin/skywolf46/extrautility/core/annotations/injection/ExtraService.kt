package skywolf46.extrautility.core.annotations.injection

/**
 * Declare class as SkywolfExtraUtility service class.
 *  Service class is frontend processor logic stage.
 *
 * Service class are highly not recommended for self-instantiation,
 *  and will be auto initialized after SkywolfExtraUtilty Core initialization.
 * Class initialization will proceed in the order of [ExtraConfig] > [ExtraModule] > [ExtraService].
 *
 * Service class is final destination of automatic class destination,
 *  and allowed for injection of [ExtraConfig], and [ExtraModule].
 *  If it not occurs circled dependency, all class can be injected to service class.
 *  If it occurs circled dependency, consider using [LateInit] annotated mutable field.
 *
 * Circled dependency of auto-injection class will throw an exception, and related class will be unregistered.
 *   Library developer must have to check dependency of classes to avoid system breakdown.
 *
 * Automatic field injection instance can be added on [skywolf46.extrautility.core.signals.pre.BeforeClassInjectSignal] signal.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExtraService