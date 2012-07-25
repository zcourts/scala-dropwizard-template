package com.fillta.odin.dropwizard.auth

import javax.servlet._
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import java.util.HashMap
import java.util.Map

/**
 * Courtney Robinson <courtney@crlog.info>
 */
class AuthFilter(session: HashMap[String, HttpSession],
                 authenticator: CookieAuthenticator)
  extends Filter {

  def init(filterConfig: FilterConfig) {
  }

  def doFilter(request: ServletRequest, response: ServletResponse, filter: FilterChain) {
    if (authenticator.authenticated(request, response, filter)) {
      //if authenticated, call the next filter
      filter.doFilter(request, response)
    } else {
      val res: HttpServletResponse = response.asInstanceOf[HttpServletResponse]
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
      val req: HttpServletRequest = request.asInstanceOf[HttpServletRequest]
      res.setStatus(401)     //for now, plain response no further processing
      //exception mapper will handle this  - apparently not...
      //http://java.net/jira/browse/JERSEY-1111
      //http://java.net/jira/browse/JERSEY-918
      //both tickets resolved but exception doesn't reach the mapper
      //TODO report possible bug in Jersey filter, exception handling
      //throw new AuthException(None, Some(req), Some(res), Some(filter))
    }
  }

  def destroy {
  }
}


