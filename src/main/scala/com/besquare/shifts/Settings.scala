package com.besquare
package shifts

import akka.actor._

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import spray.http.Uri

import scala.concurrent.duration.FiniteDuration

class Settings(config: Config) extends Extension {

  object Http {
    private val httpConfig = config.getConfig("com.besquare.shifts.http")
    val host = httpConfig.as[String]("host")
    val port = httpConfig.as[Int]("port")
  }
}

object Settings extends ExtensionId[Settings] with ExtensionIdProvider {
  override def lookup() = Settings

  override def createExtension(system: ExtendedActorSystem) = new Settings(system.settings.config)
}

trait SettingsProvider {
  def settings: Settings
}

trait ActorSettingsProvider extends SettingsProvider {
  this: Actor ⇒
  val settings = Settings(context.system)
}
