import java.nio.ByteBuffer


class Bencode {
    private data class DecodingState(val bencoded: ByteBuffer, var pos: Int = 0) {
        fun isEnd() = pos >= bencoded.array().size
        fun current() = bencoded.get(pos).convertToChar()
        fun advance() = pos++

        fun nextIndexOf(char: Char): Int {
            for (i in pos until bencoded.array().size) {
                if (bencoded.get(i).convertToChar() == char) return i
            }
            return -1
        }

        fun substring(start: Int, end: Int): String {
            return bencoded.array().decodeToString(start, end, false)
        }
    }

    val dictionaryComparator = Comparator<String> { o1, o2 ->
        if (o1.length != o2.length) return@Comparator compareValues(o1.length, o2.length)

        return@Comparator compareValues(o1, o2)
    }

    fun decodeBencode(bencoded: ByteArray): DecodingResult {
        return decodeBencode(DecodingState(ByteBuffer.wrap(bencoded)))
    }

    fun decodeBencode(bencodedString: String): DecodingResult {
        return decodeBencode(DecodingState(ByteBuffer.wrap(bencodedString.toByteArray())))
    }

    private fun decodeBencode(state: DecodingState): DecodingResult {
        val firstChar = state.current()
        return when {
            Character.isDigit(firstChar) -> DecodingResult.StringResult(decodeString(state))
            firstChar == 'i' -> DecodingResult.NumberResult(decodeNumber(state))
            firstChar == 'l' -> DecodingResult.ListResult(decodeList(state))
            firstChar == 'd' -> DecodingResult.DictionaryResult(decodeDictionary(state))

            else -> {
                println(state)
                throw Exception()
            }
        }
    }

    private fun decodeString(state: DecodingState): String {
        val firstColonIndex = state.nextIndexOf(char = ':')
        val length = Integer.parseInt(state.substring(state.pos, firstColonIndex))

        state.pos = firstColonIndex + 1 + length
        return state.substring(firstColonIndex + 1, state.pos)
    }

    private fun decodeNumber(state: DecodingState): Long {
        val endIndex = state.nextIndexOf(char = 'e')
        val numberSubstring = state.substring(state.pos + 1, endIndex)
        if (numberSubstring.length > 1 && numberSubstring[0] == '0') throw NumberFormatException()
        if (numberSubstring.startsWith("-0")) throw NumberFormatException()

        state.pos = endIndex + 1
        return numberSubstring.toLong()
    }

    private fun decodeList(state: DecodingState): List<DecodingResult> {
        val res = mutableListOf<DecodingResult>()
        state.advance()
        while (!state.isEnd() && state.current() != 'e') {
            res.add(decodeBencode(state))
        }

        state.advance()
        return res
    }

    private fun decodeDictionary(state: DecodingState): Map<String, DecodingResult> {
        val res = sortedMapOf<String, DecodingResult>(dictionaryComparator)
        state.advance()
        while (!state.isEnd() && state.current() != 'e') {
            val key = decodeString(state)
            val value = decodeBencode(state)
            res[key] = value
        }

        state.advance()
        return res
    }
}

