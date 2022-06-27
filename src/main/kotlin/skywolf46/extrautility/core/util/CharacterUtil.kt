package skywolf46.extrautility.core.util

object CharacterUtil {
    internal val languageDefine = mutableListOf<(Char) -> Boolean>()

    internal fun init() {
        // Korean language definer
        languageDefine += {
            it.isKorean(true)
        }

        languageDefine += {
            it.isEnglish()
        }
    }


    fun addLanguageDefiner(definer: (Char) -> Boolean) {
        languageDefine += definer
    }
}


fun Char.isSpecial(): Boolean {
    return CharacterUtil.languageDefine.none { it(this) }
}

fun Char.isDigit() =
    this in '0'..'9'

fun Char.isKorean(includeIncomplete: Boolean = false) =
    isCompletedKorean() || (includeIncomplete && (isKoreanConsonants() || isKoreanVowel()))

fun Char.isCompletedKorean() =
    this in '가'..'힣'

fun Char.isKoreanConsonants() =
    this in 'ㄱ'..'ㅎ'

fun Char.isKoreanVowel() =
    this in 'ㅏ'..'ㅣ'

fun Char.isEnglish() =
    isEnglishUpperCase() || isEnglishLowerCase()

fun Char.isEnglishUpperCase() =
    this in 'A'..'Z'

fun Char.isEnglishLowerCase() =
    this in 'a'..'z'

