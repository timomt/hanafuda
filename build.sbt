ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.1"

lazy val root = (project in file("."))
  .settings(
      name := "Hanafuda",
      coverageEnabled := true,
      coverageMinimumStmtTotal := 0,
      coverageMinimumBranchTotal := 0,
      coverageFailOnMinimum := true,
      coverageHighlighting := true,
      coverageExcludedPackages := ".*Hanafuda.*"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test

jacocoReportSettings := JacocoReportSettings().withFormats(JacocoReportFormats.XML, JacocoReportFormats.HTML)