import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows

class BencodeKtTest {
    private val bencode = Bencode()

    @Test
    fun decodeString() {
        bencodeStringEquals("hello", bencode.decodeBencode("5:hello"))
        bencodeStringEquals("world", bencode.decodeBencode("5:world123"))
    }

    @Test
    fun decodeInteger() {
        bencodeNumberEquals(52L, bencode.decodeBencode("i52e"))
        bencodeNumberEquals(-52L, bencode.decodeBencode("i-52e"))
        bencodeNumberEquals(4294967300L, bencode.decodeBencode("i4294967300e"))
    }

    @Test
    fun decodeLeadingZeroInteger() {
        bencodeNumberEquals(0L, bencode.decodeBencode("i0e"))
        assertThrows<NumberFormatException> { bencode.decodeBencode("i-0e") }
        assertThrows<NumberFormatException> { bencode.decodeBencode("i03e") }
    }

    @Test
    fun decodeList() {
        val expexted = listOf(
            DecodingResult.StringResult("hello"),
            DecodingResult.NumberResult(52L)
        )
        bencodeListEquals(expexted, bencode.decodeBencode("l5:helloi52ee"))
    }

    private fun bencodeStringEquals(expected: String, decoded: DecodingResult) {
        assertInstanceOf<DecodingResult.StringResult>(decoded).apply {
            assertEquals(expected, value)
        }
    }

    private fun bencodeNumberEquals(expected: Long, decoded: DecodingResult) {
        assertInstanceOf<DecodingResult.NumberResult>(decoded).apply {
            assertEquals(expected, value)
        }
    }

    private fun bencodeListEquals(expected: List<DecodingResult>, decoded: DecodingResult) {
        assertInstanceOf<DecodingResult.ListResult>(decoded).apply {
            assertEquals(expected, value)
        }
    }
}