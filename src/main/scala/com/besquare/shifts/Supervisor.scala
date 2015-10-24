package com.besquare
package shifts

import akka.actor._
import akka.io.IO
import spray.can.Http

import common._

object Supervisor {
  val props = Props(new Supervisor())
  def name = "shifts-supervisor"

  case object Start
}

class Supervisor extends Actor
    with ActorExecutionContextProvider
    with ActorCreationSupportForActors
    with ActorLogging {

  import Supervisor._
  import SupervisorStrategy._

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: ApiNotBoundException ⇒ Restart
      case t                       ⇒ super.supervisorStrategy.decider.applyOrElse(t, (_: Any) ⇒ Escalate)
    }

  def receive: Receive = {
    case Start ⇒
      val settings = Settings(context.system)

      val api = createShiftsOptimizerApi()
      context.watch(api)

      context.become(running(api))
  }

  def running(api: ActorRef): Receive = {
    case Terminated(`api`) ⇒
      log.info("API is terminated.")
    case Terminated(somethingElse) ⇒
      log.info(s"${somethingElse.path.name} is terminated.")
  }

  def createShiftsOptimizerApi() = {
    context.actorOf(Props(new Api), Api.name)
  }

}
