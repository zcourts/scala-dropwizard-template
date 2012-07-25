package com.fillta.odin.conf

import com.yammer.dropwizard.config.{HttpConfiguration, Configuration}
import org.codehaus.jackson.annotate.JsonProperty
import javax.validation.Valid
import javax.validation.constraints._
import com.fillta.odin.dropwizard.auth.Algorithms

/**
 * @author Courtney Robinson <courtney@crlog.info> @ 17/02/12
 */

class Config extends Configuration {
  @JsonProperty
  protected var log_http_errors: Boolean = true

//  @Valid
//  @NotNull
//  @JsonProperty
//  override protected var http: HttpConfig = new HttpConfig

  @Valid
  @NotNull
  @JsonProperty
  private var cookieSalt = Algorithms.md5(System.currentTimeMillis() + "")

  @Valid
  @NotNull
  @JsonProperty
  private var cookiePassword = Algorithms.md5(System.currentTimeMillis() + "")

  /**
   * @return true if the ExceptionMapper should print stack traces when an HTTP error occurs
   */
  def getLogHttpErrors: Boolean = {
    return log_http_errors
  }

  /**
   * Returns the overriden HTTP-specific section of the configuration file.
   *
   * @return HTTP-specific configuration parameters
   */
  override def getHttpConfiguration: HttpConfiguration = {
    return http
  }

  /**
   * @return The salt used when encrypting cookies
   */
  def getCookieSalt: String = {
    return cookieSalt
  }

  def getCookiePassword: String = {
    return cookiePassword
  }
}
