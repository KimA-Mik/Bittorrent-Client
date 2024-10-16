package bencode

import java.nio.ByteBuffer

class Bencoder {
    fun bencode(input: DecodingResult): ByteArray {
        val buffer = ByteBuffer.allocate(1024 * 1024)
        bencodeToBuffer(input, buffer)

        return buffer.array().sliceArray(0 until buffer.position())
    }

    private fun bencodeToBuffer(input: DecodingResult, buffer: ByteBuffer) {
        when (input) {
            is DecodingResult.DictionaryResult -> bencodeDictionaryToBuffer(input, buffer)
            is DecodingResult.ListResult -> bencodeListToBuffer(input, buffer)
            is DecodingResult.NumberResult -> bencodeNumberToBuffer(input.value, buffer)
            is DecodingResult.StringResult -> bencodeStringToBuffer(input, buffer)
        }
    }

    private fun bencodeDictionaryToBuffer(input: DecodingResult.DictionaryResult, buffer: ByteBuffer) {
        buffer.put(BENCODED_DICTIONARY.code.toByte())

        val sorted = input.value.toSortedMap(Bencode.DICTIONARY_COMPARATOR)
        for (pair in sorted) {
            bencodeStringToBuffer(pair.key, buffer)
            bencodeToBuffer(pair.value, buffer)
        }

        buffer.put(BENCODED_END.code.toByte())
    }

    private fun bencodeListToBuffer(input: DecodingResult.ListResult, buffer: ByteBuffer) {
        buffer.put(BENCODED_LIST.code.toByte())
        for (i in input.value) {
            bencodeToBuffer(i, buffer)
        }
        buffer.put(BENCODED_END.code.toByte())
    }

    private fun bencodeStringToBuffer(input: String, buffer: ByteBuffer) {
        val bytes = input.encodeToByteArray()
        val size = bytes.size.toString().encodeToByteArray()
        buffer.put(size)
        buffer.put(BENCODED_DELIMITER.code.toByte())
        buffer.put(bytes)
    }

    private fun bencodeStringToBuffer(input: DecodingResult.StringResult, buffer: ByteBuffer) {
        when (input) {
            is DecodingResult.StringResult.Binary -> {
                val size = input.value.size.toString().encodeToByteArray()
                buffer.put(size)
                buffer.put(BENCODED_DELIMITER.code.toByte())
                buffer.put(input.value)
            }

            is DecodingResult.StringResult.Utf -> bencodeStringToBuffer(input.value, buffer)
        }
    }

    private fun bencodeNumberToBuffer(number: Long, buffer: ByteBuffer) {
        buffer.put(BENCODED_NUMBER.code.toByte())
        buffer.put(number.toString().encodeToByteArray())
        buffer.put(BENCODED_END.code.toByte())
    }
}