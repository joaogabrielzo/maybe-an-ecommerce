name:= "ecommerce-maybe"

scalaVersion := "2.12.10"

organization := "com.zo"

version := "0.0.1"

javacOptions ++= Seq("-source", "1.8")

scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfatal-warnings",
    "-Yno-adapted-args",
    "-Xfuture"
)

val akka = "2.6.4"
val akkaHttp = "10.1.11"
val flyway = "3.2.1"
val scalaTest = "3.1.1"
val logbackVersion = "1.2.3"
val slick = "3.3.1"

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "org.flywaydb" % "flyway-core" % flyway,
    "org.scalatest" %% "scalatest" % scalaTest,
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-stream" % akka,
    "com.typesafe.akka" %% "akka-slf4j" % akka,
    "com.typesafe.akka" %% "akka-testkit" % akka % Test,
    "com.typesafe.akka" %% "akka-http" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp % "test",
    "com.typesafe.slick" %% "slick" % slick,
    "com.typesafe.slick" %% "slick-hikaricp" % slick,
    "org.postgresql" % "postgresql" % "9.4.1211",
    "com.h2database" % "h2" % "1.4.192" % "test"
)