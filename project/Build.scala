import sbt._
import sbt.Keys._
import scala._
import xerial.sbt.Pack._


object HueBogieBridgeBuild extends Build {

  lazy val buildSettings = Seq(
    organization := "de.lauer-online.hueBogieBridge",
    name := "hueBogieBridge",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.10",
    scalaBinaryVersion := "2.10.3"
  )

  val typesafeResolvers = Seq("Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/")

  lazy val baseSettings = Defaults.defaultSettings ++ packSettings
  lazy val defaultSettings = baseSettings ++ Seq(resolvers := typesafeResolvers)

  lazy val bogieBridgeHue = project.in(file(".")) aggregate(connectorNetatmo, connectorHue, bogieBridgeCore, bogieBridgeRuntime)

  lazy val bogieBridgeCoreName = "bogieBridgeCore"
  lazy val bogieBridgeCore = Project(
    base = file(bogieBridgeCoreName),
    id = bogieBridgeCoreName,
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.jackson ++ Dependencies.jersey ++ Dependencies.slf4j ++ Dependencies.config
    )
  )

  lazy val bogieBrigeRuntimeName = "bogieBridgeRuntime"
  lazy val bogieBridgeRuntime = Project(
    base = file(bogieBrigeRuntimeName),
    id = bogieBrigeRuntimeName,
    dependencies = Seq(bogieBridgeCore, connectorHue, connectorNetatmo, connectorLeapMotion),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.akka ++ Dependencies.slf4j ++ Dependencies.config
    ) ++ Seq(packMain := Map(bogieBrigeRuntimeName -> "de.lauer_online.bogieBridgeHue.runtime.BogieBridgeApp"),
      packJvmOpts := Map(bogieBrigeRuntimeName -> Seq("-Djava.library.path=/opt/LeapSDK/lib"))
    )
  ).dependsOn(
    bogieBridgeCore,
    connectorHue,
    connectorNetatmo,
    connectorLeapMotion
  )

  lazy val bogieBridgeCtl = Project(
    base = file("bogieBridgeCtl"),
    id = "bogieBridgeCtl",
    dependencies =  Seq(bogieBridgeCore, bogieBridgeHue),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akka ++ Dependencies.slf4j ++ Dependencies.config)
  )

  lazy val connectorHue = Project(
    base = file("connectorHue"),
    id = "connectorHue",
    dependencies = Seq(bogieBridgeCore),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akka ++ Dependencies.slf4j ++ Dependencies.config)
  ).dependsOn(bogieBridgeCore)

  lazy val connectorNetatmo = Project(
    base = file("connectorNetatmo"),
    id = "connectorNetatmo",
    dependencies = Seq(bogieBridgeCore, connectorHue),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akka ++ Dependencies.slf4j ++ Dependencies.config)
  ).dependsOn(bogieBridgeCore, connectorHue)

  lazy val connectorLeapMotion = Project(
    base = file("connectorLeapMotion"),
    id = "connectorLeapMotion",
    dependencies = Seq(bogieBridgeCore, connectorHue),
    settings = defaultSettings ++ Seq(libraryDependencies ++= Dependencies.akka ++ Dependencies.slf4j)
  ).dependsOn(bogieBridgeCore, connectorHue)

}

object Dependencies {
  object Compile {
    val akka_actor = "com.typesafe.akka" %% "akka-actor" % "2.2.1"
    val akka_remote = "com.typesafe.akka" %% "akka-remote" % "2.2.1"
    val slf4j_api = "org.slf4j" % "slf4j-api" % "1.7.5"
    val logback = "ch.qos.logback" % "logback-classic" % "1.0.0"
    val typesafe_config = "com.typesafe" % "config" % "1.0.2"

    val jersey_core = "com.sun.jersey" % "jersey-core" % "1.17.1"
    val jersey_client =  "com.sun.jersey" % "jersey-client" % "1.17.1"

    val jackson_databind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.3"
    val jackson_scala_module = "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.2.1"
    val jackson_json_providers = "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.2.3"
  }

  import Compile._

  val akka = Seq(akka_actor, akka_remote)
  val slf4j = Seq(slf4j_api,logback)
  val config = Seq(typesafe_config)

  val jackson = Seq(jackson_databind, jackson_scala_module, jackson_json_providers)
  val jersey = Seq(jersey_core, jersey_client)
}
