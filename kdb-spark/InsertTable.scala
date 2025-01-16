package com.kdbspark.writekdb

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


object InsertTable{

	def main(args:Array[String]):Unit={
		//args(0) : a number of sensor 

		if(args.length < 1){
			throw new IllegalArgumentException("No argument.")
		}

		val sensorID = "`sensor_"+args(0)

		//Connection	
		println("Start to create SprarkSession")
		val conf = new SparkConf().
			setAppName("InsertKdbTable").
			setMaster("spark://HOST:PORT").
			set("spark.default.parallelism","2").
			set("spark.jars","/opt/spark/jars/kdbspark_2.11-2.4.4.jar")
		val spark = SparkSession.builder().
                        config(conf).
                        getOrCreate()


		//Connection information ( Connect to q process for Spark on Kx for Sensors )
		var conn = new mutable.HashMap[String, String]
		conn.put("host", "HOSTNAME")
		conn.put("port", "PORT")
		conn.put("loglevel", "debug")
		println("Connection Info: " + conn)

		val sc = spark.sparkContext
		val r = scala.util.Random
		val schema = StructType(StructField("readingID",LongType) ::
        		StructField("sensorID",StringType)::
        		StructField("sensorTm",TimestampType)::
        		StructField("sensorVals",DataTypes.createArrayType(DoubleType,false))::
        		Nil)
	

	

		try{	
			//Generate Data
			val data = List(Row(r.nextInt(1000000).toLong,sensorID,getCurrentdateTimeStamp,Array(r.nextFloat(),r.nextFloat(),r.nextFloat())),
				Row(r.nextInt(1000000).toLong,sensorID,getCurrentdateTimeStamp,Array(r.nextFloat(),r.nextFloat(),r.nextFloat())),
				Row(r.nextInt(1000000).toLong,sensorID,getCurrentdateTimeStamp,Array(r.nextFloat(),r.nextFloat(),r.nextFloat())),
				Row(r.nextInt(1000000).toLong,sensorID,getCurrentdateTimeStamp,Array(r.nextFloat(),r.nextFloat(),r.nextFloat())),
				Row(r.nextInt(1000000).toLong,sensorID,getCurrentdateTimeStamp,Array(r.nextFloat(),r.nextFloat(),r.nextFloat())))
	
			val df=spark.createDataFrame(sc.parallelize(data),schema);

		    df.show();
			
			//Ingest the data to kdb
			df.write.format("kdb").options(conn).
        		option("batchsize",5).
        		option("func",".sp.ingest").
        		option("writeaction","append").
        		option("sdlsrcname","sparkIngest").
        		option("sdlmsgtype",".kxstr.readingSpark").
        		option("feed","feed1").
        		save
			println("Insert table is completed.")
		}
		finally{
			spark.stop()
		}
	}
	def getCurrentdateTimeStamp: JTimestamp ={
       			val today:java.util.Date = Calendar.getInstance.getTime
        		val timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        		val now:String = timeFormat.format(today)
        		val re = java.sql.Timestamp.valueOf(now)
        		re
     	}

}
