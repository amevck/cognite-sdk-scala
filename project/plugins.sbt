//addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.5")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.7")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.0")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.2")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.25")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.9.0"
