import sbtcrossproject.CrossPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "apps.EDF"
ThisBuild / version      := "0.1.0-SNAPSHOT"

// ------------------------------------------------------------
// shared : code commun (Types, Wires)
// ------------------------------------------------------------

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("apps/shared"))
  .settings(
    name := "shared",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "3.1.0"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.6.0"
    )
  )

// ------------------------------------------------------------
// jvm : logique côté serveur / console
// ------------------------------------------------------------

lazy val jvm = (project in file("jvm"))
  .dependsOn(shared.jvm)
  .settings(
    name := "equipefoot-jvm"
  )

// ------------------------------------------------------------
// js : UI Scala.js
// ------------------------------------------------------------

import org.scalajs.linker.interface.ModuleKind

lazy val js = (project in file("apps/js"))   // <<< ICI le chemin change
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(shared.js)
  .settings(
    name := "equipefoot-js",
    scalaJSUseMainModuleInitializer := false,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.NoModule)),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.6.0",
      "com.lihaoyi"  %%% "upickle"     % "3.1.0"
    )
  )




// ------------------------------------------------------------
// projet racine
// ------------------------------------------------------------

lazy val root = (project in file("."))
  .aggregate(shared.jvm, shared.js, jvm, js)
  .settings(
    name := "EquipeFoot"
  )
