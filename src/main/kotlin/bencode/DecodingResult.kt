package bencode

sealed interface DecodingResult {
    sealed interface StringResult : DecodingResult {
        @JvmInline
        value class Utf(val value: String) : StringResult

        @JvmInline
        value class Binary(val value: ByteArray) : StringResult
    }

    @JvmInline
    value class NumberResult(val value: Long) : DecodingResult

    @JvmInline
    value class ListResult(val value: List<DecodingResult>) : DecodingResult

    @JvmInline
    value class DictionaryResult(val value: Map<String, DecodingResult>) : DecodingResult

    fun getValue(): Any {
        return when (this) {
            is ListResult -> this.value.map(DecodingResult::getValue)
            is NumberResult -> this.value
            is StringResult.Utf -> this.value
            is StringResult.Binary -> this.value
            is DictionaryResult -> this.value.mapValues { it.value.getValue() }
        }
    }
}