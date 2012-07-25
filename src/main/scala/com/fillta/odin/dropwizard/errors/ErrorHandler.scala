package com.fillta.odin.dropwizard.errors

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import com.fillta.odin.conf.Config

/**
 * Courtney Robinson <courtney@crlog.info>
 */
class ErrorHandler(config: Config) {

  def handleException {
    if (exception.isInstanceOf[WebApplicationException]) {
      val wae: WebApplicationException = exception.asInstanceOf[WebApplicationException]
      val r: Response = wae.getResponse
      //.`type`(MediaType.APPLICATION_JSON)
      if (r != null) {
        r.getStatus match {
          case 404 =>
          case 401 =>
        }
      }
    }
  }

  /**
   * Sets the exception that caused this error view to be rendered.
   * Subclasses are expected to evaluate this object and create their views accordingly
   *
   * @param e The exception that cause this view to be rendered
   */
  def setError(e: Throwable) {
    exception = e
  }

  def getResponse = response

  /**
   * Should the exception mapper print stack traces?
   *
   * @return
   */
  def doLog: Boolean = {
    return config.getLogHttpErrors
  }

  protected var exception: Throwable = null
  protected var response: Response = null
}