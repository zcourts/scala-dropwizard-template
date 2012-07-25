package com.fillta.odin.dropwizard.auth

import javax.servlet.{FilterChain, ServletResponse, ServletRequest}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

/**
 * Courtney Robinson <courtney@crlog.info>
 */

case class AuthException(msg: Option[String] = None,
                         request: Option[HttpServletRequest] = None,
                         response: Option[HttpServletResponse] = None,
                         filter: Option[FilterChain] = None)
  extends WebApplicationException(
    Response.status(401)
      .entity("Unauthorized")
      .build()
  ) {
}
