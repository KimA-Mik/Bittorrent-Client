import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows

class BencodeKtTest {
    private val bencode = Bencode()

    @Test
    fun `decode string`() {
        bencodeStringEquals("hello", bencode.decodeBencode("5:hello"))
        bencodeStringEquals("world", bencode.decodeBencode("5:world123"))
    }

    @Test
    fun `decode number`() {
        bencodeNumberEquals(52L, bencode.decodeBencode("i52e"))
        bencodeNumberEquals(-52L, bencode.decodeBencode("i-52e"))
        bencodeNumberEquals(4294967300L, bencode.decodeBencode("i4294967300e"))
    }

    @Test
    fun `decode number with leading zero`() {
        bencodeNumberEquals(0L, bencode.decodeBencode("i0e"))
        assertThrows<NumberFormatException> { bencode.decodeBencode("i-0e") }
        assertThrows<NumberFormatException> { bencode.decodeBencode("i03e") }
    }

    @Test
    fun `decode list`() {
        val expected = listOf(
            DecodingResult.StringResult("hello"),
            DecodingResult.NumberResult(52L)
        )
        bencodeListEquals(expected, bencode.decodeBencode("l5:helloi52ee"))
    }

    @Test
    fun `decode nested list`() {
        var expected = listOf<DecodingResult>(
            listResultOf(
                DecodingResult.NumberResult(435L), DecodingResult.StringResult("orange")
            )
        )
        bencodeListEquals(expected, bencode.decodeBencode("lli435e6:orangeee"))

        expected = listOf(
            listResultOf(DecodingResult.NumberResult(4)),
            DecodingResult.NumberResult(5)
        )
        bencodeListEquals(expected, bencode.decodeBencode("lli4eei5ee"))
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

    private fun listResultOf(vararg results: DecodingResult): DecodingResult.ListResult {
        return DecodingResult.ListResult(results.toList())
    }
}