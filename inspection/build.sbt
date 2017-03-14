organization in ThisBuild := "nl.codecentric"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

// Own dependencies
val mariaDb = "org.mariadb.jdbc" % "mariadb-java-client" % "1.4.6"
val hikariCp = "com.zaxxer" % "HikariCP" % "2.4.5"
val cats = "org.typelevel" %% "cats" % "0.9.0"

// Dependencies by Lagom
val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `inspection` = (project in file("."))
  .aggregate(`inspection-api`, `inspection-impl`, `inspection-database`)

lazy val `inspection-api` = (project in file("inspection-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `inspection-impl` = (project in file("inspection-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceJdbc,
      lagomScaladslTestKit,
      mariaDb,
      hikariCp,
      cats,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`inspection-api`)

lazy val `inspection-database` = (project in file("inspection-database"))
  .settings(
    libraryDependencies ++= Seq(
      mariaDb
    )
  )
  .settings(
    flywayLocations := Seq("filesystem:inspection-database/src/main/resources/db/migration"),
    flywayUrl := "jdbc:mariadb://127.0.0.1:3306/FRAMEWORKDB",
    flywayUser := "COFFEE",
    flywayPassword := "secret-coffee-pw",
    flywayBaselineOnMigrate := true
  )


initialCommands := """|import nl.codecentric.inspection._
                      |""".stripMargin
