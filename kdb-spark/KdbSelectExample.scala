package com.kdbspark.main

//import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._
import scala.collection.mutable._
//import scala.collection.JavaConversions._

import org.apache.spark.rdd.RDD
import scala.reflect.ClassTag
//import org.apache.spark.sql.catalyst.InternalRow

import java.util

object KdbSelectExample{

	def main(args:Array[String]){

		val sparkConf = new SparkConf().
                        setAppName("KdbContextExample").
                        setMaster("spark://HOST:PORT").
                        set("spark.default.parallelism","2").
				set("spark.jars","kdbspark-app_2.11-0.1.jar")

		//Get the singleton instance of SparkSession
		val spark = SparkSession.
				builder().
				config(sparkConf).
				getOrCreate()

		//Define connection configuration
		val conf = new HashMap[String, String]
		conf.put("host", "HOST_NAME")
       	conf.put("port", "PORT")
        conf.put("loglevel", "debug")

        println("Connection Info: " + conf)
 
        //Define table schema
		val schema = StructType(List(
			StructField("shardid", IntegerType, false),
			StructField("jcolumn", LongType, false),
			StructField("fcolumn", DoubleType, false), 
			StructField("pcolumn", TimestampType, false)))

		//Generate RDD for parameters
		val rdd = spark.sparkContext.parallelize(0 to 1, 2)
		//Make kdbContext instance
		val kdbContext = new KdbContext(spark,conf)
		//Get RDD[InternalRow]  
		val getRDD= kdbContext.callAPI[Int]("selectTable2",schema,rdd)
		//Example for RDD action	
		getRDD.collect()
		
		
        }

}
