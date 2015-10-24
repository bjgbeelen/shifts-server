package com.besquare
package common

trait AskTimeoutProvider {
  implicit val timeout: akka.util.Timeout
}

trait DefaultAskTimeoutProvider extends AskTimeoutProvider {
  import scala.concurrent.duration._

  implicit val timeout = akka.util.Timeout(3 seconds)
}
