ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.1"

lazy val root = (project in file("."))
  .settings(
      name := "Hanafuda",
      coverageEnabled := true,
      coverageMinimumStmtTotal := 100,
      coverageMinimumBranchTotal := 100,
      coverageFailOnMinimum := true,
      coverageHighlighting := true,
      coverageExcludedPackages := ".*Hanafuda.*"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % "test"