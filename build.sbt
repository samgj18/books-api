name := "books"

version := "0.1"

scalaVersion := "2.13.5"

scalacOptions ++= Seq(
  "-Ymacro-annotations",
  "-Wconf:cat=unused:info"
)

libraryDependencies ++= Seq(
  compilerPlugin(
    "org.typelevel" %% "kind-projector" % "0.12.0"
      cross CrossVersion.full
  ),
  "ch.qos.logback"         % "logback-classic"        % "1.2.3",
  "co.fs2"                %% "fs2-core"               % "3.0.6",
  "io.circe"              %% "circe-core"             % "0.14.1",
  "io.circe"              %% "circe-generic"          % "0.14.1",
  "io.circe"              %% "circe-refined"          % "0.14.1",
  "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.16.0",
  "com.github.pureconfig" %% "pureconfig"             % "0.16.0",
  "eu.timepit"            %% "refined"                % "0.9.25",
  "eu.timepit"            %% "refined-cats"           % "0.9.25",
  "io.estatico"           %% "newtype"                % "0.4.4",
  "org.tpolecat"          %% "doobie-core"            % "1.0.0-M5",
  "org.tpolecat"          %% "doobie-postgres"        % "1.0.0-M5",
  "org.tpolecat"          %% "doobie-hikari"          % "1.0.0-M5",
  "org.typelevel"         %% "cats-effect"            % "3.2.1",
  "org.http4s"            %% "http4s-dsl"             % "0.23.0",
  "org.http4s"            %% "http4s-blaze-server"    % "0.23.0",
  "org.http4s"            %% "http4s-circe"           % "0.23.0",
  "org.flywaydb"           % "flyway-core"            % "6.0.7",
  "tf.tofu"               %% "derevo-cats"            % "0.12.5",
  "tf.tofu"               %% "derevo-cats-tagless"    % "0.12.5",
  "tf.tofu"               %% "derevo-circe-magnolia"  % "0.12.5",
  "tf.tofu"               %% "tofu-core-higher-kind"  % "0.10.2"
)
