package com.kx.test;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import kx.c;
import kx.c.KException;

public class APACSeminar {

	public static void main(String[] args) throws KException, IOException {
		// TODO Auto-generated method stub
		
		// Connect to kdb+
		c c=new c("HOST","PORT","USER:PASSWORD");

		
		// Send the sync message
//		Object result=c.k("f",1);
//		System.out.println("result is "+result);
		
		String[] cols = new String[] {"a","b","c"};
		int c1 = 1;
		double c2 = 0.1;
//		Object[] c3 = new Object[]{" ".toCharArray()};
		Timestamp[] c3 = new Timestamp[] {Timestamp.from(Instant.now()),Timestamp.valueOf("2022-05-19 00:10:00.0000"), Timestamp.valueOf("2022-05-19 00:00:00.0000")};
		Object[] obj = {c1,c2,c3};

		c.Dict dict = new c.Dict(cols, obj);
		c.Flip table = new c.Flip(dict);
		c.ks("insert", "t", table);
		
	
		
		// Execute the qsql to q process 
//		Object result=c.k("select from tb");
//		c.Flip table=(c.Flip)result; // c.Flip data type is compatiable with kdb table
//		String cols[] = table.x;     // column name
//		Object obj[] = table.y;		 // each column data
//		String[] sym = (String[])obj[0];
//		float[] ask = (float[])obj[1];
//		float[] bid = (float[])obj[2];
//		for(int i=0; i<cols.length; i++)
//			System.out.print(cols[i] + "\t\t");
//		System.out.println();
//		System.out.println("=========================================");
//		for(int i=0; i<obj.length; i++) {
//			System.out.print(sym[i] + "\t\t");
//			System.out.print(ask[i] + "\t");
//			System.out.println(bid[i]);
//
//		}

		
		
		//Send the async message
	//	c.ks("g",2,3);
		
		// Close the connection
		c.close();

	}

}
