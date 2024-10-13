sealed interface DecodingResult {
    @JvmInline
    value class StringResult(val value: String) : DecodingResult

    @JvmInline
    value class NumberResult(val value: Long) : DecodingResult

    @JvmInline
    value class ListResult(val value: List<DecodingResult>) : DecodingResult

    fun getValue(): Any {
        return when (this) {
            is ListResult -> this.value.map(DecodingResult::getValue)
            is NumberResult -> this.value
            is StringResult -> this.value
        }
    }
}