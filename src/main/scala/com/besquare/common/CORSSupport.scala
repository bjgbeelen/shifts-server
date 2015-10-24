package com.besquare.common

import spray.http._
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.routing._

// see also https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS
trait CORSSupport {
  this: HttpService ⇒

  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
  private val optionsCorsHeaders = List(
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Access-Control-Request-Headers, Access-Control-Request-Method, Connection, Host, Referer, User-Agent"),
    `Access-Control-Max-Age`(1728000))

  def cors[T]: Directive0 = mapRequestContext { ctx ⇒
    ctx.withRouteResponseHandling({
      //It is an option requeset for a resource that responds to some other method
      case Rejected(x) if (ctx.request.method.equals(HttpMethods.OPTIONS) && x.exists(_.isInstanceOf[MethodRejection])) ⇒ {
        val allowedMethods: List[HttpMethod] = x.collect { case rejection: MethodRejection ⇒ rejection.supported }
        ctx.complete(HttpResponse().withHeaders(
          `Access-Control-Allow-Methods`(OPTIONS, allowedMethods: _*) :: allowOriginHeader ::
            optionsCorsHeaders
        ))
      }
    }).withHttpResponseHeadersMapped { headers ⇒
      allowOriginHeader :: headers

    }
  }
}
