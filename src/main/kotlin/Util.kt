fun Byte.convertToChar() = this.toInt().toChar()

inline fun <T> T?.onNull(action: () -> T): T {
    if (this == null) return action()
    return this
}