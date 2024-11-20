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


libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-base" % "22.0.2",
  "org.openjfx" % "javafx-controls" % "22.0.2",
  "org.openjfx" % "javafx-fxml" % "22.0.2",
  "org.openjfx" % "javafx-graphics" % "22.0.2",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

// Fork JVM for JavaFX options
Compile / run / fork := true

// JavaFX module options
javaOptions ++= Seq(
  "--module-path", "target/scala-3.5.1/classes", // Adjust if needed
  "--add-modules", "javafx.controls,javafx.fxml"
)

// JDK Compatibility
javacOptions ++= Seq("--release", "21")

jacocoReportSettings := JacocoReportSettings().withFormats(JacocoReportFormats.XML, JacocoReportFormats.HTML)