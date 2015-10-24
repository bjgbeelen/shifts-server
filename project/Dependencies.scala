import sbt._
import sbt.Keys._

object Dependencies {

  lazy val dependencySettings = Seq(
    resolvers := Seq(
      "Sonatype Releases"   at "http://oss.sonatype.org/content/repositories/releases",
      "Spray Repository"    at "http://repo.spray.io/",
      "Maven Central"       at "https://repo1.maven.org/maven2/"
    )
  )

  val akkaVersion = "2.4.0"
  val sprayVersion = "1.3.3"

  val scalaReflect = "org.scala-lang" % "scala-reflect" % Build.targetScalaVersion

  val akkaActor                = "com.typesafe.akka"       %% "akka-actor"                    % akkaVersion
  val akkaPersistence          = "com.typesafe.akka"       %% "akka-persistence"              % akkaVersion
  val akkaSlf4j                = "com.typesafe.akka"       %% "akka-slf4j"                    % akkaVersion
  val akkaPersistenceCassandra = "com.github.krasserm"     %% "akka-persistence-cassandra"    % "0.4"
  val sprayClient              = "io.spray"                %% "spray-client"                  % sprayVersion
  val sprayRouting             = "io.spray"                %% "spray-routing"                 % sprayVersion
  val sprayUtil                = "io.spray"                %% "spray-util"                    % sprayVersion
  val sprayCache               = "io.spray"                %% "spray-caching"                 % sprayVersion
  val sprayCan                 = "io.spray"                %% "spray-can"                     % sprayVersion
  val sprayTestkit             = "io.spray"                %% "spray-testkit"                 % sprayVersion
  val sprayJson                = "io.spray"                %% "spray-json"                    % "1.3.2"
  val logback                  = "ch.qos.logback"          %  "logback-classic"               % "1.1.3"
  val ficus                    = "net.ceedubs"             %% "ficus"                         % "1.1.2"
  val logstashLogbackEncoder   = "net.logstash.logback"    %  "logstash-logback-encoder"      % "4.5.1"
  val scalatest                = "org.scalatest"           %% "scalatest"                     % "2.2.5"
  val oscarLinprog             = "oscar"                   %% "oscar-linprog"                 % "1.1.0"
  val jodaTime                 = "com.github.nscala-time"  %% "nscala-time"                   % "2.2.0"
  
  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "it,test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")
}
