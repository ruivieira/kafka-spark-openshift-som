name := "kafka-spark-openshift-som"

version := "0.1"

scalaVersion := "2.11.11"

val sparkVersion = "2.3.0"

organization := "org.bonesbrigade"

version := "0.1-SNAPSHOT"

resolvers += "Will's bintray" at "https://dl.bintray.com/willb/maven/"

mainClass in assembly := Some( "org.bonesbrigade.skeletons.kafkasparkopenshift.Main" )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.3.0",
  "org.apache.spark" %% "spark-mllib" % "2.3.0",
  "org.apache.kafka" % "kafka-clients" % "1.1.1",
  "io.radanalytics" %% "silex" % "0.2.0"
)