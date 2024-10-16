package bencode

import convertToChar
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
            return bencoded.array().decodeToString(start, end, true)
        }

        fun slice(start: Int, end: Int): ByteArray {
            return bencoded.array().sliceArray(start until end)
        }
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
            Character.isDigit(firstChar) -> decodeStringResult(state)
            firstChar == BENCODED_NUMBER -> DecodingResult.NumberResult(decodeNumber(state))
            firstChar == BENCODED_LIST -> DecodingResult.ListResult(decodeList(state))
            firstChar == BENCODED_DICTIONARY -> DecodingResult.DictionaryResult(decodeDictionary(state))

            else -> {
                println(state)
                throw Exception()
            }
        }
    }

    private fun decodeStringResult(state: DecodingState): DecodingResult.StringResult {
        return try {
            DecodingResult.StringResult.Utf(tryDecodeString(state))
        } catch (e: Exception) {
            DecodingResult.StringResult.Binary(decodeStringToByteArray(state))
        }
    }

    private fun tryDecodeString(state: DecodingState): String {
        val firstColonIndex = state.nextIndexOf(char = BENCODED_DELIMITER)
        val length = Integer.parseInt(state.substring(state.pos, firstColonIndex))

        val str = state.substring(firstColonIndex + 1, firstColonIndex + 1 + length)
        state.pos = firstColonIndex + 1 + length
        return str
    }

    private fun decodeStringToByteArray(state: DecodingState): ByteArray {
        val firstColonIndex = state.nextIndexOf(char = BENCODED_DELIMITER)
        val length = Integer.parseInt(state.substring(state.pos, firstColonIndex))

        state.pos = firstColonIndex + 1 + length
        return state.slice(firstColonIndex + 1, state.pos)
    }

    private fun decodeNumber(state: DecodingState): Long {
        val endIndex = state.nextIndexOf(char = BENCODED_END)
        val numberSubstring = state.substring(state.pos + 1, endIndex)
        if (numberSubstring.length > 1 && numberSubstring[0] == '0') throw NumberFormatException()
        if (numberSubstring.startsWith("-0")) throw NumberFormatException()

        state.pos = endIndex + 1
        return numberSubstring.toLong()
    }

    private fun decodeList(state: DecodingState): List<DecodingResult> {
        val res = mutableListOf<DecodingResult>()
        state.advance()
        while (!state.isEnd() && state.current() != BENCODED_END) {
            res.add(decodeBencode(state))
        }

        state.advance()
        return res
    }

    private fun decodeDictionary(state: DecodingState): Map<String, DecodingResult> {
        val res = sortedMapOf<String, DecodingResult>(DICTIONARY_COMPARATOR)
        state.advance()
        while (!state.isEnd() && state.current() != BENCODED_END) {
            val key = decodeStringToByteArray(state).decodeToString()
            val value = decodeBencode(state)
            res[key] = value
        }

        state.advance()
        return res
    }

    companion object {
        val DICTIONARY_COMPARATOR = Comparator<String> { o1, o2 ->
            return@Comparator compareValues(o1, o2)
        }
    }
}

