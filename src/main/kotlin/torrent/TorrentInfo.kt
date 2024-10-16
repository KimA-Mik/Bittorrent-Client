package torrent

import bencode.DecodingResult

data class TorrentInfo(
    //size of the file in bytes, for single-file torrents
    val length: Long,
    //suggested name to save the file / directory as
    val name: String,
    val pieceLength: Long,
    val pieces: List<ByteArray>
) {
    companion object {
        fun extract(dict: Map<String, DecodingResult>): TorrentInfo? {
            val lengthResult = dict[LENGTH_KEY]
            if (lengthResult !is DecodingResult.NumberResult) {
                return null
            }

            val nameResult = dict[NAME_KEY]
            if (nameResult !is DecodingResult.StringResult.Utf) {
                return null
            }

            val pieceLengthResult = dict[PIECE_LENGTH_KEY]
            if (pieceLengthResult !is DecodingResult.NumberResult) {
                return null
            }

            val piecesResult = dict[PIECES_KEY]
            if (piecesResult !is DecodingResult.StringResult.Binary) {
                return null
            }

            val pieces = mutableListOf<ByteArray>()
            val hashStep = 20
            for (pos in 0 until piecesResult.value.size step hashStep) {
                pieces.add(
                    piecesResult.value.sliceArray(pos until pos + hashStep)
                )
            }

            return TorrentInfo(
                length = lengthResult.value,
                name = nameResult.value,
                pieceLength = pieceLengthResult.value,
                pieces = pieces
            )
        }

        private const val LENGTH_KEY = "length"
        private const val NAME_KEY = "name"
        private const val PIECE_LENGTH_KEY = "piece length"
        private const val PIECES_KEY = "pieces"
    }
}