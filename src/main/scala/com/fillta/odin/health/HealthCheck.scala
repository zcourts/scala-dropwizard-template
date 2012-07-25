package com.fillta.odin.health

import javax.ws.rs.GET
import javax.ws.rs.Path
/**
 * @author Courtney Robinson <courtney@crlog.info>
 */
@Path("/health/default")
class HealthCheck(name: String) extends com.yammer.metrics.core.HealthCheck(name) {

  @GET
  def check: com.yammer.metrics.core.HealthCheck.Result = {
    return com.yammer.metrics.core.HealthCheck.Result.healthy
  }
}