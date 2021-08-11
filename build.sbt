name := "book-rental"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "ch.qos.logback"         % "logback-classic"     % "1.2.3",
  "com.github.ghostdogpr" %% "caliban"             % "1.1.1",
  "com.github.ghostdogpr" %% "caliban-cats"        % "1.1.1",
  "com.github.ghostdogpr" %% "caliban-http4s"      % "1.1.1",
  "org.typelevel"         %% "cats-effect"         % "3.2.1",
  "co.fs2"                %% "fs2-core"            % "3.0.6",
  "io.estatico"           %% "newtype"             % "0.4.4",
  "eu.timepit"            %% "refined"             % "0.9.25",
  "eu.timepit"            %% "refined-cats"        % "0.9.25",
  "org.http4s"            %% "http4s-dsl"          % "0.23.0",
  "org.http4s"            %% "http4s-blaze-server" % "0.23.0"
)
