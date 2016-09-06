logLevel := Level.Warn

resolvers += "Flyway" at "https://flywaydb.org/repo"
// The Typesafe repository
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/maven-releases/"
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.12")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
