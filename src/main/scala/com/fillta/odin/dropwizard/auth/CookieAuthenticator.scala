package com.fillta.odin.dropwizard.auth

import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import com.fillta.odin.dropwizard.auth.info.crlog.amigos.auth.AESEncrypter

/**
 * An authenticator is used to know when to deny or allow access to resources
 * using the {@link RequireAuth} annotation
 */
class CookieAuthenticator(cipher: AESEncrypter) {
  def authenticated(request: ServletRequest, response: ServletResponse, filter: FilterChain): Boolean = {
    val req: HttpServletRequest = request.asInstanceOf[HttpServletRequest]
    for (cookie <- req.getCookies) {
      if (cookie.getName == Algorithms.md5("uid")) {
        val v: String = cipher.decrypt(cookie.getValue)
        if (v == null) return false
        return v.length > 0
      }
    }
    return false
  }

}

