package com.kdbspark.readkdb

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import scala.collection.mutable
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._
import java.sql.{Timestamp => JTimestamp, Date => JDate}
import java.time.LocalDateTime
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat


object SelectTable{

	def main(args:Array[String]):Unit={

		if(args.length < 1){
			throw new IllegalArgumentException("No argument.")
		}
		//Connection	
		println("Start to create SprarkSession")
		val conf = new SparkConf().
			setAppName("SelectKdbTable").
			setMaster("spark://HOST:PORT").
			set("spark.default.parallelism","2").
			set("spark.jars","/opt/spark/jars/kdbspark_2.11-2.4.4.jar")
		 val spark = SparkSession.builder().
                        config(conf).
                        getOrCreate()


		var api = ".kxstr.getReadingTable"
		//Connection information ( Connect to q process for Spark on Kx for Sensors )
		var conn = new mutable.HashMap[String, String]
		conn.put("host", "HOST_PORT")
		conn.put("port", "PORT")
		conn.put("loglevel", "debug")
		println("Connection Info: " + conn)

		//initial schema
		var s = "readingID long,sensorID string,sensorTm timestamp,sensorVals double"
		println("Schema Definition: " + s)

		//args option with hashmap
		var dict = new mutable.HashMap[String, String]
		dict.put("sensorid", args(0))
		dict.put("startts", args(1))
		dict.put("endts", args(2))
		println("Args:  " + dict)
		try{	
			val df = spark.read.format("kdb").
				schema(s).
				options(conn).
				options(dict).
				option("api",api).
				option("func",".sp.request").load.toDF()

			df.show()
		}
		finally{
			spark.stop()
		}
	}
}
