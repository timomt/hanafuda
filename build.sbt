ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.1"

lazy val root = (project in file("."))
  .settings(
      name := "Hanafuda",
      mainClass in Compile := Some("scala.Hanafuda"),
      coverageEnabled := true,
      coverageMinimumStmtTotal := 0,
      coverageMinimumBranchTotal := 0,
      coverageFailOnMinimum := true,
      coverageHighlighting := true,
      coverageExcludedPackages := ".*Hanafuda.*"
  )



libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "23.0.1-R34",
//  "org.openjfx" % "javafx-base" % "23",
//  "org.openjfx" % "javafx-controls" % "23",
//  "org.openjfx" % "javafx-fxml" % "23",
//  "org.openjfx" % "javafx-graphics" % "23",
//  "org.openjfx" % "javafx-media" % "23",
//  "org.openjfx" % "javafx-web" % "23",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "22" classifier osName)
}
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