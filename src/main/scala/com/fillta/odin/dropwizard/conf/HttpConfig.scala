package com.fillta.odin.dropwizard.conf

import com.yammer.dropwizard.config.HttpConfiguration

/**
 * Courtney Robinson <courtney@crlog.info>
 */
class HttpConfig extends HttpConfiguration {
  override def getPort: Int = {
    return port
  }

  override def getAdminPort: Int = {
    return adminPort
  }

  //  @Min(80)
  //  @Max(65535)
  //  @JsonProperty
  //  override protected var port = 80
  //  @Min(1025)
  //  @Max(65535)
  //  @JsonProperty
  //  override protected var adminPort = 9091
}


