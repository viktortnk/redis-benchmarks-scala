name := "redis-benchmarks"

version := "0.1"

scalaVersion := "2.13.3"

Compile / run / fork := true

run / javaOptions += "-Xmx4G"

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")

scalacOptions ++= Seq(
  "-deprecation",           // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8",                  // Specify character encoding used by source files.
  "-explaintypes",          // Explain type errors in more detail.
  "-feature",               // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds"   // Allow higher-kinded types
)

libraryDependencies ++= Seq(
  "co.fs2"            %% "fs2-core"      % "2.4.0",
  "org.typelevel"     %% "cats-effect"   % "2.1.4",
  "io.monix"          %% "monix-eval"    % "3.2.2",
  "com.twitter"       %% "finagle-redis" % "20.8.1",
  "io.lettuce"         % "lettuce-core"  % "5.3.3.RELEASE",
  "redis.clients"      % "jedis"         % "3.3.0",
  "org.apache.commons" % "commons-lang3" % "3.11",
)
