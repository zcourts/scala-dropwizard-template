package com.fillta.odin.dropwizard.auth
package info.crlog.amigos.auth

import com.yammer.dropwizard.logging.Log
import sun.misc.BASE64Decoder
import sun.misc.BASE64Encoder
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.Key

/**
 * @author http://www.exampledepot.com/egs/javax.crypto/desstring.html
 * @author          http://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption/992413#992413
 * @author         Courtney Robinson <courtney@crlog.info>
 *                 Implementation base from http://www.digizol.org/2009/10/java-encrypt-decrypt-jce-salt.html
 */
class AESEncrypter(passwordStr: String,
                   salt: String = "not_secure",
                   ITERATIONS: Int = 2,
                   encoding: String = "UTF8",
                   ALGORITHM: String = "AES"
                    ) {
  protected var ecipher: Cipher = null
  protected var dcipher: Cipher = null
  protected var password: Array[Byte] = Array[Byte]('N', 'O', 'T')
  private val log: Log = Log.forClass(classOf[AESEncrypter])
  try {
    password = passwordStr.getBytes(encoding)
    val key = generateKey
    ecipher = Cipher.getInstance(ALGORITHM)
    ecipher.init(Cipher.ENCRYPT_MODE, key)
    dcipher = Cipher.getInstance(ALGORITHM)
    dcipher.init(Cipher.DECRYPT_MODE, key)
  }
  catch {
    case e: Exception => {
      log.error(e, "Unable to initializer AES encryptor")
    }
  }

  def encrypt(value: String): String = {
    var valueToEnc: String = null
    var eValue: String = value
    try {
      for (i <- 0 until ITERATIONS) {
        valueToEnc = salt + eValue
        val encValue: Array[Byte] = ecipher.doFinal(valueToEnc.getBytes)
        eValue = new BASE64Encoder().encode(encValue)
      }
    } catch {
      case e: Exception => {
        log.error(e, "Unable to encrypt value")
      }
    }
    return eValue
  }

  def decrypt(value: String): String = {
    var dValue: String = null
    try {
      var valueToDecrypt: String = value
      for (i <- 0 until ITERATIONS) {
        val decordedValue: Array[Byte] = new BASE64Decoder().decodeBuffer(valueToDecrypt)
        val decValue: Array[Byte] = dcipher.doFinal(decordedValue)
        dValue = new String(decValue).substring(salt.length)
        valueToDecrypt = dValue
      }
    }
    catch {
      case e: Exception => {
        log.error(e, "Unable to decrypt value")
      }
    }
    return dValue
  }

  private def generateKey: Key = {
    val key: Key = new SecretKeySpec(password, ALGORITHM)
    return key
  }
}