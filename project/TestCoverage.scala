import sbt._
import sbt.Keys._
import scoverage.ScoverageSbtPlugin._

object TestCoverage {
    lazy val testCoverageSetings = Seq(
    ScoverageKeys.coverageHighlighting := true,
    ScoverageKeys.coverageExcludedPackages := ".*Main.*;.*BuildInfo.*"
)}
