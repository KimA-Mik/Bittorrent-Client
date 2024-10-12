class Bencode {
    private data class DecodingState(val bencodedString: String, var pos: Int = 0) {
        fun isEnd() = pos >= bencodedString.length
        fun current() = bencodedString[pos]
        fun advance() = pos++
    }

    fun decodeBencode(bencodedString: String): Any {
        return decodeBencode(DecodingState(bencodedString))
    }

    private fun decodeBencode(state: DecodingState): Any {
        val firstChar = state.bencodedString[state.pos]
        return when {
            Character.isDigit(firstChar) -> decodeString(state)
            firstChar == 'i' -> decodeNumber(state)
            firstChar == 'l' -> decodeList(state)

            else -> {
                println(state)
                TODO("Only strings are supported at the moment")
            }
        }
    }

    private fun decodeString(state: DecodingState): String {
        val firstColonIndex = state.bencodedString.indexOf(char = ':', startIndex = state.pos)
        val length = Integer.parseInt(state.bencodedString.substring(state.pos, firstColonIndex))

        state.pos = firstColonIndex + 1 + length
        return state.bencodedString.substring(firstColonIndex + 1, state.pos)
    }

    private fun decodeNumber(state: DecodingState): Long {
        val endIndex = state.bencodedString.indexOf(char = 'e', startIndex = state.pos)
        val numberSubstring = state.bencodedString.substring(state.pos + 1, endIndex)
        if (numberSubstring.length > 1 && numberSubstring[0] == '0') throw NumberFormatException()
        if (numberSubstring.startsWith("-0")) throw NumberFormatException()

        state.pos = endIndex + 1
        return numberSubstring.toLong()
    }

    private fun decodeList(state: DecodingState): List<Any> {
        val res = mutableListOf<Any>()
        state.advance()
        while (!state.isEnd() && state.current() != 'e') {
            res.add(decodeBencode(state))
        }

        return res
    }
}

