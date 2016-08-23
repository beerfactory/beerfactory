package org.beerfactory.backend.utils

import java.util.Base64

object Codecs {
  def base64Encode(bytes: Array[Byte]): String = Base64.getEncoder.encodeToString(bytes)
  def base64Decode(str: String): Array[Byte] = Base64.getDecoder.decode(str)

  def hexEncode(bytes: Array[Byte], sep: String = ""): String = bytes.map("%02x".format(_)).mkString(sep)
  def hexDecode(hex: String): Array[Byte] = {
    hex.replaceAll("[^0-9A-Fa-f]", "").sliding(2, 2).toArray.map(Integer.parseInt(_, 16).toByte)
  }
}