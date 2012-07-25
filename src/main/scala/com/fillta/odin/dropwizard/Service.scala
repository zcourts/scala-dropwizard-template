package com.fillta.odin.dropwizard

import auth.{RequireAuth, AuthFilter, CookieAuthenticator}
import com.sun.jersey.core.spi.scanning.PackageNamesScanner
import com.sun.jersey.spi.scanning.AnnotationScannerListener
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.logging.Log
import errors.ErrorHandler
import javax.ws.rs._
import javax.ws.rs.ext.Provider
import java.lang.reflect.Method
import java.util._
import providers.ConfigProvider
import providers.info.crlog.amigos.providers.{AbstractInjectableProvider, CipherProvider}
import javax.servlet.http.HttpSession
import com.yammer.dropwizard.ScalaService
import com.fillta.odin.conf.Config
import com.fillta.odin.health.HealthCheck
import com.fillta.odin.resources.BaseResource

/**
 * @author Courtney Robinson <courtney@crlog.info>
 */
abstract class Service[C <: Config](name: String) extends ScalaService[C](name) {

  protected var errorHandler: ExceptionMapper = null
  protected var config: Config = null
  protected var environment: Environment = null
  protected var resourcePackages: List[String] = new ArrayList[String]
  protected var providerPackages: List[String] = new ArrayList[String]
  protected var log: Log = Log.forClass(classOf[Service[_ <: Config]])
  protected var cipherProvider: CipherProvider = null
  protected var filteredClasses: Set[java.lang.Class[_]] = new HashSet[Class[_]]
  protected var authFilter: AuthFilter = null
  protected var sessions: HashMap[String, HttpSession] = new HashMap[String, HttpSession]()
  protected var authenticator: CookieAuthenticator = null

  /**
   * Add a package to the set of provider packages that contain your provider
   * classes
   *
   * @param path The package path. Both java.lang and java/lang are valid
   *             package paths
   */
  def addProviderPackage(path: String) {
    providerPackages.add(path)
  }

  /**
   * Add a package to the set of resourcePackages that contain your controller
   * classes
   *
   * @param path The package path. Both java.lang and java/lang are valid
   *             package paths
   */
  def addResourcePackage(path: String) {
    resourcePackages.add(path)
  }


  protected def initialize(configuration: C, environment: Environment) {
    this.config = configuration.asInstanceOf[Config]
    this.environment = environment
    val configProvider: ConfigProvider = new ConfigProvider(config)
    environment.addProvider(configProvider)
    cipherProvider = new CipherProvider(config.getCookiePassword, config.getCookieSalt)
    environment.addProvider(cipherProvider)

    authenticator = new CookieAuthenticator(cipherProvider.getValue(null))
    authFilter = new AuthFilter(sessions, authenticator)


    errorHandler = new ExceptionMapper(config)
    environment.addProvider(errorHandler)
    environment.addHealthCheck(new HealthCheck(name))
    this.initializeService
    initializeProviders //first
    initializeResources //second
    initializeFilters //third...order matters
  }


  private def initializeProviders {
    if (providerPackages.size > 0) {
      val scanner: PackageNamesScanner = new PackageNamesScanner(providerPackages.toArray(new Array[String](providerPackages.size)))
      val scanListener: AnnotationScannerListener = new AnnotationScannerListener(classOf[Provider])
      scanner.scan(scanListener)
      import scala.collection.JavaConversions._
      for (c <- scanListener.getAnnotatedClasses) {
        if (!classOf[ConfigProvider].isAssignableFrom(c) && !classOf[CipherProvider].isAssignableFrom(c)) {
          environment.addProvider(c)
          log.info(String.format("Provider %s added", c.getName))
        }
      }
    }
  }

  private def initializeResources {
    if (resourcePackages.size > 0) {
      val scanner: PackageNamesScanner = new PackageNamesScanner(resourcePackages.toArray(new Array[String](resourcePackages.size)))
      val scanListener: AnnotationScannerListener = new AnnotationScannerListener(classOf[Path])
      scanner.scan(scanListener)
      import scala.collection.JavaConversions._
      for (c <- scanListener.getAnnotatedClasses) {
        environment.addResource(c)
        log.info(String.format("Resource %s added", c.getName))
      }
    }
  }

  private def initializeFilters {
    if (resourcePackages.size > 0) {
      val scanner: PackageNamesScanner = new PackageNamesScanner(resourcePackages.toArray(new Array[String](resourcePackages.size)))
      val scanListener: AnnotationScannerListener = new AnnotationScannerListener(classOf[Path])
      scanner.scan(scanListener)
      filteredClasses.addAll(scanListener.getAnnotatedClasses)
    }
    val it = filteredClasses.iterator()
    while (it.hasNext) {
      val c = it.next()
      var protectEntireClass: Boolean = false
      if (c.isAnnotationPresent(classOf[RequireAuth])) {
        protectEntireClass = true
      }
      if (protectEntireClass) {
        val methods = c.getMethods
        for (i <- 0 until methods.length) {
          val method = methods(i)
          protectResourceMethod(c, method)
        }
      } else {
        val methods = c.getMethods
        for (i <- 0 until methods.length) {
          val method = methods(i)
          if (method.isAnnotationPresent(classOf[RequireAuth])) {
            protectResourceMethod(c, method)
          }
        }
      }
    }
  }

  private def protectResourceMethod(c: Class[_], method: Method) {
    var base: String = ""
    if (c.isAnnotationPresent(classOf[Path])) {
      base = (c.getAnnotation(classOf[Path])).value
    }
    if (method.isAnnotationPresent(classOf[OPTIONS]) || method.isAnnotationPresent(classOf[DELETE]) || method.isAnnotationPresent(classOf[GET]) || method.isAnnotationPresent(classOf[PUT]) || method.isAnnotationPresent(classOf[HEAD]) || method.isAnnotationPresent(classOf[POST])) {
      val p: Path = method.getAnnotation(classOf[Path])
      val path: String = if (p == null) "" else p.value
      if (base.endsWith("/") && path.startsWith("/")) {
        base = base.substring(1)
      }
      if (!base.endsWith("/") && !path.startsWith("/") && !path.isEmpty) {
        base += "/"
      }
      val fullpath: String = base + path
      environment.addFilter(authFilter, fullpath)
      log.info(String.format("Filter %s added (%s)", c.getName, fullpath))
    }
  }

  /**
   * Override this method to manually initialize anything you need to.
   * Controllers etc. The environment and config properties have already been
   * initialized so you can use them
   */
  protected def initializeService {
  }

  /**
   * Set the error handler used for HTTP errors
   */
  def setErrorHandler[E <: ErrorHandler](handler: E) {
    errorHandler.setHandler(handler)
  }
}