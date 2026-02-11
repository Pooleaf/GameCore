package net.pooleaf.gamereplay.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object CompressionUtil {

    private const val GZIP_MAGIC_BYTE_1 = 0x1f.toByte()
    private const val GZIP_MAGIC_BYTE_2 = 0x8b.toByte()

    /**
     * 바이트 배열을 GZIP으로 압축합니다.
     */
    fun compressGzip(data: ByteArray): ByteArray {
        ByteArrayOutputStream().use { byteStream ->
            GZIPOutputStream(byteStream).use { gzipStream ->
                gzipStream.write(data)
            }
            return byteStream.toByteArray()
        }
    }

    /**
     * GZIP 압축된 바이트 배열을 해제합니다.
     */
    fun decompressGzip(data: ByteArray): ByteArray {
        ByteArrayInputStream(data).use { byteStream ->
            GZIPInputStream(byteStream).use { gzipStream ->
                return gzipStream.readBytes()
            }
        }
    }

    /**
     * 바이트 배열이 GZIP 압축되었는지 확인합니다.
     * GZIP 매직 넘버(0x1f 0x8b)로 시작하는지 검사합니다.
     */
    fun isGzipCompressed(data: ByteArray): Boolean {
        return data.size >= 2 && data[0] == GZIP_MAGIC_BYTE_1 && data[1] == GZIP_MAGIC_BYTE_2
    }

}
