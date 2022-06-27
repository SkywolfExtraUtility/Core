package skywolf46.extrautility.core.enumeration

import skywolf46.extrautility.core.util.isEnglish
import skywolf46.extrautility.core.util.isEnglishLowerCase
import skywolf46.extrautility.core.util.isEnglishUpperCase
import skywolf46.extrautility.core.util.isKorean

enum class CharacterFilter(private val checker: (Char) -> Boolean) {
    ENGLISH({
        it.isEnglish()
    }),
    KOREAN({
        it.isKorean()
    }),
    UPPERCASE({
        it.isEnglishUpperCase()
    }),
    LOWERCASE({
        it.isEnglishLowerCase()
    });

}