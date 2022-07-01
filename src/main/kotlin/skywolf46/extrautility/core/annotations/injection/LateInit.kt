package skywolf46.extrautility.core.annotations.injection

/**
 * Mark field as late init field.
 *  Late init field will initialize after all class initialized, and cannot be used while initialization.
 *
 * Late init injected field always not occurs circular dependency problem.
 *  It's not recommended using [LateInit] to avoid circular dependency,
 *  nevertheless, if logic need it, it can be used to alternate of constructor injection.
 *
 * Late init marked field must have to not final, and mutable.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class LateInit