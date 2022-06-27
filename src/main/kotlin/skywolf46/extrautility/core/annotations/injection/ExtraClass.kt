package skywolf46.extrautility.core.annotations.injection

/**
 * Declare class as SkywolfExtraUtility instantiatable class.
 *
 * Instantiable class is auto injectable, non-singleton class.
 *  Manual class instantiating is not recommended for [ExtraClass] annotated class,
 *  and must have to instantiated with [skywolf46.extrautility.core.util.InjectionUtil.instantiate] method.
 *  This operation can be replaced with [skywolf46.extrautility.core.util.instantiate] global extension.
 */
annotation class ExtraClass()
