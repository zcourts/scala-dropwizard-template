# Scala Dropwizard Maven Template

It includes a base structure to get started with as well as a few useful utilities.

See the com.fillta.odin.dropwizard package for all the extensions this adds.

* RequireAuth annotation - If this annotation is added to a class then all its methods are protected. Only authenticated requests can be made to the class.
	The annotation can also be added just to a single method and only that method will be protected.

* AuthFilter - This extension takes advantage of Jersey's "filters" to provide the authentication described above
	By default only a "CookieAuthenticator" exists.
	*NOTE: The authentication mechanism needs to be improved

* AESEncrypter - Is provides encryption and decryption of strings

* Algorithms provides methods to get and md5 and sha1 hashes, e.g. Algorithms.md5() and Algorithms.sha1()

* ErrorHandler - to be completed but will provide mapping of exceptions to HTTP responses.

* ExceptionMapper - Catches any* uncaught exceptions thrown and tries to map those exceptions to a sane HTTP response.
					It uses the ErrorHandler described above to do this (in part)

* AbstractInjectableProvider - provides a base for any injectable provider you wish to have. This allows you to use
							Jersey's @Context to inject instance of objects into your service.

* ConfigProvider - Allows Your configuration to be injected into any class that needs it. Its useful since
					you only get access to the config in Dropwizard's initialize method in the Service subclass.
					This way you can access your config without worrying about how to pass it to whatever class may need it

* Service - Extends Dropwizard's ScalaService class to encapsulate all of the above and more.
			It also automatically does things like, scan a given package and automatically add all
			Resources, Providers and Filters. All you have to do is provide the path of the package, can have
			as many packages as needed.

# RPM packaging

 If you wish to package your service as an RPM

 * uncomment the maven plugin in pom.xml after the following comment:
 "__<!--uncomment to build an RPM from your project-->__"  AND change
 <group>Services/my-service</group> to replace my-service with your service's name

 * __Rename__ src/main/init.d/my-service to whatever your service is called


