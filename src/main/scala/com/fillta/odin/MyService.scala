package com.fillta.odin

import conf.Config
import dropwizard.providers.info.crlog.amigos.providers.AbstractInjectableProvider
import dropwizard.Service
import resources.BaseResource

/**
 * @author Courtney Robinson <courtney@crlog.info>
 */

object MyService extends Service[Config]("Odin") {

  override protected def initializeService {
    //initialize
    //all resources in the package will be added
    addResourcePackage(classOf[BaseResource].getPackage.getName)
    //add all providers
    addProviderPackage(classOf[AbstractInjectableProvider[_]].getPackage.getName)
  }

}