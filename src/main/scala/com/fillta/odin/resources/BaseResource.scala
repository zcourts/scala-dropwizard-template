package com.fillta.odin.resources

import javax.ws.rs.{Produces, GET, Path}
import javax.ws.rs.core.{MediaType, Response}
import com.fillta.odin.dropwizard.auth.RequireAuth

/**
 * Courtney Robinson <courtney@crlog.info>
 */
@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/")
class BaseResource {
  @GET
  @RequireAuth
  def test = Response.status(200).entity("Hello world").build()
}
