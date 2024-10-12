fun decodeBencode(bencodedString: String): Any {
    val firstChar = bencodedString[0]
    return when {
        Character.isDigit(firstChar) -> decodeString(bencodedString)
        firstChar == 'i' -> decodeNumber(bencodedString)

        else -> TODO("Only strings are supported at the moment")
    }
}

private fun decodeString(bencodedString: String): String {
    val firstColonIndex = bencodedString.indexOfFirst { it == ':' }
    val length = Integer.parseInt(bencodedString.substring(0, firstColonIndex))
    return bencodedString.substring(firstColonIndex + 1, firstColonIndex + 1 + length)
}

private fun decodeNumber(bencodedString: String): Long {
    val endIndex = bencodedString.indexOfFirst { it == 'e' }
    return bencodedString.substring(1, endIndex).toLong()
}
