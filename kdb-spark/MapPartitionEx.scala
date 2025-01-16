package com.ex.mp

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import scala.collection.mutable
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._

object MapPartitionEx{

        def main(args:Array[String]){

                val sparkConf = new SparkConf().
                        setAppName("MapPartitionKdbTable").
                        setMaster("spark://HOST:PORT").
                        set("spark.default.parallelism","2")

                //Build the SparkSession
                val spark = SparkSession.
                                builder().
                                config(sparkConf).
                                getOrCreate()

                //Define table schema
                val s = "shardid int,jcolumn long,fcolumn double,pcolumn timestamp"

                //Configuration for kdb+ connection
                val conf = new mutable.HashMap[String, String]
                conf.put("host", "HOST_NAME;HOST_NAME")
                conf.put("port", "PORT;PORT")
                conf.put("loglevel", "debug")
                println("Connection Info: " + conf)

                //Initialize datasourcereader
                val data = spark.read.format("kdb").
                options(conf).
                option("func","selectTable").
                option("param","1;3").
                option("numPartitions","2").
                schema(s)

                //Actual reading as rdd
                val mapPart = data.load().rdd

                //Print the number of RDD. It should be same with `numPartitions`
                println("Number of RDD : " + mapPart.getNumPartitions)

                //Print RDD[Row] in each executor
                mapPart.foreach(println)

        }

}
