package com.fillta.odin.dropwizard

import com.yammer.dropwizard.logging.Log
import errors.ErrorHandler
import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.ext.Provider
import com.fillta.odin.conf.Config
import com.sun.jersey.api.NotFoundException

/**
 * Courtney Robinson <courtney@crlog.info>
 */
@Provider
class ExceptionMapper(config: Config) extends javax.ws.rs.ext.ExceptionMapper[Throwable] {
  protected var handler: ErrorHandler = new ErrorHandler(config)
  protected var log: Log = Log.forClass(classOf[ExceptionMapper])

  def toResponse(throwableCause: Throwable): Response = {
    handler.setError(throwableCause)
    handler.handleException
    val r: Response = handler.getResponse
    throwableCause match {
      case ex: NotFoundException => {
        if (r == null) {
          Response.status(404).`type`(MediaType.APPLICATION_JSON).build()
        } else {
          r
        }
      }
      case _ => {
        if (handler.doLog) {
          val trace: Array[StackTraceElement] = throwableCause.getStackTrace
          val b: StringBuilder = new StringBuilder
          for (traceElement <- trace) b.append(traceElement)
          if (r != null) {
            this.log.warn(throwableCause, "Error, handled returning status %s with view %s Stacktrace %s".format(r.getStatus, r.getEntity.getClass.getName, b.toString))
          } else {
            log.warn(throwableCause, "Stacktrace : %s".format(b))
          }
        }
        r
      }
    }
  }

  /**
   * Sets the handler used to manage errors
   *
   * @param handler
   */
  def setHandler(handler: ErrorHandler) {
    this.handler = handler
  }

}

