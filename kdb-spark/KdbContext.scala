package com.kdbspark.main

//import org.apache.yetus.audience.InterfaceAudience
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.{SerializableWritable, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.DataFrameReader
import org.apache.spark.sql.catalyst.InternalRow

import scala.collection.mutable._
import scala.collection.JavaConversions._
import scala.reflect.ClassTag 
import org.apache.spark.sql.types._

import org.apache.spark.sql.execution.vectorized.OnHeapColumnVector
import org.apache.spark.sql.vectorized.ColumnVector
import org.apache.spark.sql.vectorized.ColumnarBatch
import org.apache.spark.sql.catalyst.util.DateTimeUtils

import java.nio.charset.StandardCharsets
import java.sql.{Timestamp => JTimestamp, Date => JDate}
import java.util.UUID

import java.util
import scala.collection.JavaConversions._
import kx.c

/**
  * KdbContext is a façade for kdb+ operations
  * like select, insert
  *
  * KdbContext will take the responsibilities
  * of disseminating the configuration information
  * to the working and managing the life cycle of Connections.
 */

//@InterfaceAudience.Public
class KdbContext(@transient val spark:SparkSession,	
		 @transient val config:HashMap[String,String]) 
//		 @transient val config:util.Map[String,String]) 
	extends Serializable{

  //Scatter the connection information on each executor
	val broadcastedConf = spark.sparkContext.broadcast(config)

	def callAPI[T:ClassTag](apiName:String,schema:StructType,rdd:RDD[T]):RDD[InternalRow] = {
		//func: Iterator[Int] => Iterator[InternalRow]	
    //Run `mapFunc` in each executor with RDD
		rdd.mapPartitions(x => mapFunc[T](apiName,broadcastedConf,schema,x.next()))
	}
	def mapFunc[T](apiName:String,
		       connConf:Broadcast[HashMap[String,String]],
//		       connConf:Broadcast[util.Map[String,String]],
		       schema:StructType,
		       param:T):Iterator[InternalRow]={	

		val conf = connConf.value
		conf.put("func",apiName)
		conf.put("param",param.toString)
		conf.put("partitionid",param.toString)
		
		val javaConf = mapAsJavaMap(conf)
		println("Configuration of this executor : " + javaConf);
		var kdbFilters = new Array[Object](0)

		val obj = KdbCall.query(javaConf,kdbFilters,schema)	
		val table = obj.asInstanceOf[c.Flip]
		var colnames:Array[String] = table.x
		var coldata:Array[Object] = table.y
		val rowN = c.n(table)
		val acv = new Array[ColumnVector](schema.length)
		for(i <-0 to schema.length-1){
			acv(i) = populateColumn(schema(i), rowN, coldata(i))}
		val batch  = new ColumnarBatch(acv)
		batch.setNumRows(rowN)
		batch.numRows
		batch.column(0).getInts(0,rowN)
		batch.rowIterator
	}

}
