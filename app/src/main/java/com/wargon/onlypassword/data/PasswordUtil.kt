import kotlin.random.Random

fun generatePwd(
    length: Int,
    includeNumber: Boolean = true,
    includeCharacter: Boolean = true,
    includeSpecialChar: Boolean = true,
    startsWithCharacter: Boolean = false
): String {
    require(length > 0) { "密码长度必须大于0" }

    val lowercaseLetters = "abcdefghijkmnpqrstuvwxyz" // 排除容易混淆的 l, o
    val uppercaseLetters = "ABCDEFGHJKLMNPQRSTUVWXYZ" // 排除容易混淆的 I, O
    val numbers = "23456789" // 排除容易混淆的 0, 1
    val specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"

    // 根据参数构建可用的字符集
    val charPool = buildString {
        if (includeCharacter) {
            append(lowercaseLetters)
            append(uppercaseLetters)
        }
        if (includeNumber) {
            append(numbers)
        }
        if (includeSpecialChar) {
            append(specialChars)
        }
    }.takeIf { it.isNotEmpty() }
        ?: throw IllegalArgumentException("至少需要启用一种字符类型")

    // 确保密码包含至少一个每种要求的字符类型
    val requiredChars = mutableListOf<CharSequence>().apply {
        if (includeCharacter) {
            add(lowercaseLetters)
            add(uppercaseLetters)
        }
        if (includeNumber) {
            add(numbers)
        }
        if (includeSpecialChar) {
            add(specialChars)
        }
    }

    // 生成随机密码
    return buildString {
        // 1. 如果要求以字母开头，先添加一个随机字母
        if (startsWithCharacter && includeCharacter) {
            val letters = lowercaseLetters + uppercaseLetters
            append(letters.random())
        }

        // 2. 确保包含每种要求的字符类型（如果还未包含）
        requiredChars.forEach { charSet ->
            if (!startsWithCharacter || charSet !in listOf(lowercaseLetters, uppercaseLetters)) {
                append(charSet.random())
            }
        }

        // 3. 填充剩余长度
        val remainingLength = length - this.length
        repeat(remainingLength) {
            append(charPool.random())
        }

        // 4. 打乱字符顺序
        if(startsWithCharacter) {
            val chars = this.toMutableList()
            if (chars.size > 1) {
                val subList = chars.subList(1, chars.size).shuffled()
                for (i in 1 until chars.size) {
                    chars[i] = subList[i-1]
                }
            }
            clear()
            chars.forEach { append(it) }
        }else {
            val shuffled = this.toList().shuffled()
            clear()
            shuffled.forEach { append(it) }
        }
    }
}

// 扩展函数：从CharSequence中随机选取一个字符
fun CharSequence.random(): Char {
    return this[Random.nextInt(length)]
}