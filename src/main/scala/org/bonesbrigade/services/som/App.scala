package org.bonesbrigade.services.som

import breeze.linalg.DenseVector
import io.radanalytics.silex.som.SOM
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types._


object App {

  def main(args: Array[String]): Unit = {
    val brokers = "localhost:9092"
    val intopic = "bones-brigade"
    val outtopic = "topic2"
    println("kafka-spark-openshift-som starting")
    println(" - brokers: " + brokers)
    println(" - in topic: " + intopic)
    println(" - out topic: " + outtopic)

    /* acquire a SparkSession object */
    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("KafkaSparkOpenShiftSOM")
      .getOrCreate()

    /* configure the operations to read the input topic */
    val records = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", brokers)
      .option("subscribe", intopic)
      .option("failOnDataLoss", value = false)
      .load()
      .select(functions.column("value").cast(DataTypes.StringType).alias("value"))



    // start an empty SOM
    val som = SOM.random(xdim = 2, ydim = 1, fdim = 1)


    val schema = StructType(StructField("key", StringType) :: StructField("value", StringType) :: Nil)

    val transformed = records.map { case Row(value:String) =>
      val example = DenseVector[Double](value.toDouble)
      Row("1", som.closestWithSimilarity(example)._2.toString)
    }(RowEncoder(schema))

    /* configure the output stream */
    val writer = records
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", brokers)
      .option("topic", outtopic)
      .outputMode("update")
      .option("checkpointLocation", "/tmp")
      .start()

    /* begin processing the input and output topics */
    writer.awaitTermination()
  }

}
