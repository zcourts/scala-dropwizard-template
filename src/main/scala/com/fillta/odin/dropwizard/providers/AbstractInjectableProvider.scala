package com.fillta.odin.dropwizard.providers
package info.crlog.amigos.providers

import com.sun.jersey.core.spi.component.ComponentContext
import com.sun.jersey.core.spi.component.ComponentScope
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable
import com.sun.jersey.spi.inject.Injectable
import com.sun.jersey.spi.inject.InjectableProvider
import javax.ws.rs.core.Context
import java.lang.reflect.Type

/**
 * @author http://codahale.com/what-makes-jersey-interesting-injection-providers/
 *         Courtney Robinson <courtney@crlog.info>
 */
abstract class AbstractInjectableProvider[E](t: Type) extends AbstractHttpContextInjectable[E] with InjectableProvider[Context, Type] {


  def getInjectable(ic: ComponentContext, a: Context, c: Type): Injectable[E] = {
    if (c == t) {
      return getInjectable(ic, a)
    }
    return null
  }

  def getInjectable(ic: ComponentContext, a: Context): Injectable[E] = {
    return this
  }

  def getScope: ComponentScope = {
    return ComponentScope.PerRequest
  }
}