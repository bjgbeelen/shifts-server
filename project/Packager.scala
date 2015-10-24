import sbt.Keys._
import sbt._
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

object Packager {
  lazy val packagerSettings = packageArchetype.java_server ++ Seq(
    name in Rpm := "ShiftsOptimizer",
    packageSummary in Linux := "Shifts Optimizer",
    packageDescription in Rpm := "This package provides the Shifts Optimizer.",
    rpmVendor := "Be Square Development",
    rpmLicense := Some("All rights reserved."),
    rpmRelease := Option(sys.props("version")) getOrElse "1",
    rpmBrpJavaRepackJars := false
  )
}
