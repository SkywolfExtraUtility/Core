package skywolf46.extrautility.core.enumeration.reflection

import skywolf46.extrautility.core.abstraction.JvmFilter
import skywolf46.extrautility.core.definition.JvmModifier
import skywolf46.extrautility.core.util.safeKotlin
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KVisibility

enum class FieldFilter : JvmFilter<Field> {
    INSTANCE_NOT_REQUIRED {
        override fun isSatisfied(data: Field): Boolean {
            return Modifier.STATIC.isSatisfied(data) ||
                    ClassFilter.INSTANCE_NOT_REQUIRED.isSatisfied(data.declaringClass)
        }
    },

    INSTANCE_REQUIRED {
        override fun isSatisfied(data: Field): Boolean {
            return !INSTANCE_NOT_REQUIRED.isSatisfied(data)
        }
    };

    enum class Modifier : JvmFilter<Field> {
        STATIC {
            override fun isSatisfied(data: Field): Boolean {
                return JvmModifier.isStatic(data.modifiers)
            }
        },

        OPEN {
            override fun isSatisfied(data: Field): Boolean {
                return data.safeKotlin()?.isOpen ?: false
            }
        },

        FINAL {
            override fun isSatisfied(data: Field): Boolean {
                return JvmModifier.isStatic(data.modifiers)
            }
        },

        PUBLIC {
            override fun isSatisfied(data: Field): Boolean {
                return JvmModifier.isPublic(data.modifiers)
            }
        },

        PROTECTED {
            override fun isSatisfied(data: Field): Boolean {
                return JvmModifier.isProtected(data.modifiers)
            }
        },

        PRIVATE {
            override fun isSatisfied(data: Field): Boolean {
                return JvmModifier.isPrivate(data.modifiers)
            }
        },

        INTERNAL {
            override fun isSatisfied(data: Field): Boolean {
                return data.safeKotlin()?.visibility == KVisibility.INTERNAL
            }
        }
    }
}