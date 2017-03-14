// The Lagom plugin
addSbtPlugin("com.lightbend.lagom" % "lagom-sbt-plugin" % "1.3.1")

addSbtPlugin("com.geirsson"      % "sbt-scalafmt"    % "0.5.6")
addSbtPlugin("io.spray"          % "sbt-revolver"    % "0.8.0")

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.1.1")
resolvers += "Flyway" at "https://flywaydb.org/repo"
