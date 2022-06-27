package skywolf46.extrautility.core.enumeration.reflection

import skywolf46.extrautility.core.abstraction.JvmFilter
import skywolf46.extrautility.core.definition.JvmModifier
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.jvm.javaMethod

enum class MethodFilter : JvmFilter<KFunction<*>> {
    INSTANCE_NOT_REQUIRED {
        override fun isSatisfied(data: KFunction<*>): Boolean {
            return Modifier.STATIC.isSatisfied(data) ||
                    ClassFilter.INSTANCE_NOT_REQUIRED.isSatisfied(data.javaMethod!!.declaringClass.kotlin)
        }
    },
    INSTANCE_REQUIRED {
        override fun isSatisfied(data: KFunction<*>): Boolean {
            return !INSTANCE_REQUIRED.isSatisfied(data)
        }
    };

    enum class Modifier : JvmFilter<KFunction<*>> {
        OPEN {
            override fun isSatisfied(data: KFunction<*>): Boolean {
                return data.isOpen
            }
        },
        FINAL {
            override fun isSatisfied(data: KFunction<*>): Boolean {
                return data.isFinal
            }
        },
        PUBLIC {
            override fun isSatisfied(data: KFunction<*>): Boolean {
                return data.visibility == KVisibility.PUBLIC
            }
        },
        PROTECTED {
            override fun isSatisfied(data: KFunction<*>): Boolean {
                return data.visibility == KVisibility.PROTECTED
            }
        },
        PRIVATE {
            override fun isSatisfied(data: KFunction<*>): Boolean {
                return data.visibility == KVisibility.PRIVATE
            }
        },
        INTERNAL {
            override fun isSatisfied(data: KFunction<*>): Boolean {
                return data.visibility == KVisibility.INTERNAL
            }
        },

        STATIC {
            override fun isSatisfied(data: KFunction<*>): Boolean {
                return JvmModifier.isStatic(data.javaMethod!!.modifiers)
            }
        }
    }
}