package skywolf46.extrautility.core.annotations.injection

import skywolf46.extrautility.core.enumeration.Stage

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AfterInitialize(val stage: Stage = Stage.POST)