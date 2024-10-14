package bencode

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows

class BencodeTest {
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
    fun `decode empty list`() = bencodeListEquals(expected = emptyList(), decoded = bencode.decodeBencode("le"))

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

    @Test
    fun `decode dictionary`() {
        val expected = sortedMapOf(
            comparator = bencode.dictionaryComparator,
            "foo" to DecodingResult.StringResult("bar"),
            "hello" to DecodingResult.NumberResult(52L)
        )
        bencodeDictionaryEquals(expected, bencode.decodeBencode("d3:foo3:bar5:helloi52ee"))
    }

    @Test
    fun `decode empty dictionary`() = bencodeDictionaryEquals(
        expected = emptyMap(),
        decoded = bencode.decodeBencode("de"),
    )

    @Test
    fun `decode complex values`() = bencodeDictionaryEquals(
        expected = mapOf(
            "spam" to listResultOf(
                DecodingResult.StringResult("a"),
                DecodingResult.StringResult("b"),
            )
        ),
        decoded = bencode.decodeBencode("d4:spaml1:a1:bee")
    )

    @Test
    fun `decode long values`() {
        bencodeDictionaryEquals(
            expected = mapOf(
                "publisher" to DecodingResult.StringResult("bob"),
                "publisher-webpage" to DecodingResult.StringResult("www.example.com"),
                "publisher.location" to DecodingResult.StringResult("home")
            ),
            decoded = bencode.decodeBencode("d9:publisher3:bob17:publisher-webpage15:www.example.com18:publisher.location4:homee")
        )
    }

    @Test
    fun `decode nested dictionaries`() = bencodeDictionaryEquals(
        expected = mapOf(
            "inner_dict" to dictionaryResultOf(
                "key1" to DecodingResult.StringResult("value1"),
                "key2" to DecodingResult.NumberResult(42),
                "list_key" to listResultOf(
                    DecodingResult.StringResult("item1"),
                    DecodingResult.StringResult("item2"),
                    DecodingResult.NumberResult(3)
                )
            )
        ),
        decoded = bencode.decodeBencode("d10:inner_dictd4:key16:value14:key2i42e8:list_keyl5:item15:item2i3eeee")
    )

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

    private fun bencodeDictionaryEquals(expected: Map<String, DecodingResult>, decoded: DecodingResult) {
        assertInstanceOf<DecodingResult.DictionaryResult>(decoded).apply {
            assertEquals(expected, value)
        }
    }

    private fun listResultOf(vararg results: DecodingResult): DecodingResult.ListResult {
        return DecodingResult.ListResult(results.toList())
    }

    private fun dictionaryResultOf(vararg pairs: Pair<String, DecodingResult>): DecodingResult.DictionaryResult {
        return DecodingResult.DictionaryResult(mapOf(*pairs))
    }
}