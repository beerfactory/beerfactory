package utils

import java.nio.ByteBuffer
import java.util.{Base64, UUID}

object Codecs {
  def toBase64(uuid: UUID): String = {
    val (high, low) = (uuid.getMostSignificantBits, uuid.getLeastSignificantBits)
    val buffer      = ByteBuffer.allocate(java.lang.Long.BYTES * 2)
    buffer.putLong(high)
    buffer.putLong(low)
    Codecs.toBase64(buffer.array())
  }

  def toBase64(bytes: Array[Byte]): String =
    Base64.getUrlEncoder.encodeToString(bytes).split("=")(0)
  def fromBase64(str: String): Array[Byte] = Base64.getUrlDecoder.decode(str)

  def toHex(bytes: Array[Byte], sep: String = ""): String =
    bytes.map("%02x".format(_)).mkString(sep)
  def fromHex(hex: String): Array[Byte] = {
    hex.replaceAll("[^0-9A-Fa-f]", "").sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
  }
}
