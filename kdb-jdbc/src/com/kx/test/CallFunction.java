package com.kx.test;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class CallFunction {
	
	static String DRIVER; // driver name
	static String URL; // connection argument format for q process
	static String USER; // user name
	static String PASSWORD; // password

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
				// Read the configuration file
				String resource = "config/jdbc.properties";
				Properties config = new Properties();
				try {
					FileInputStream files = new FileInputStream(resource);
					config.load(new java.io.BufferedInputStream(files));
					
					DRIVER =config.getProperty("driver");
					URL =config.getProperty("url");
					USER =config.getProperty("username");
					PASSWORD =config.getProperty("password");

				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				

				Connection conn=null;
			    Statement stmt=null;
			    

			    try{
			      Class.forName(DRIVER);

			      System.out.println("Connecting to database...");
			      conn=DriverManager.getConnection(URL,USER,PASSWORD);
			      System.out.println("Connected database successfully...");

			     //Call function to select table
			     //The result should be the table to assign the Result set
			     CallableStatement cstmt = conn.prepareCall("q)selectTb[]");
			     //CallableStatement cstmt = conn.prepareCall("q)select from trade");
			     ResultSet rs = cstmt.executeQuery();

			     System.out.println("Print the result Table: ");
			     System.out.println("sym\task\tbid");
			     System.out.println("=".repeat(30));
			     while(rs.next()){
			    	 String sym = rs.getString("sym"); 
			    	 double ask = rs.getDouble("ask");
			    	 double bid = rs.getDouble("bid");

			    	 System.out.print(sym + "\t");
			    	 System.out.print(ask + "\t");
			    	 System.out.println(bid );
			     }
	 
			     			     
			    conn.close();

			    }catch(SQLException se){
			      se.printStackTrace();
			    }catch(Exception e){
			      e.printStackTrace();
			    }finally{
			      try{
			        if(stmt!=null)
			          stmt.close();
			      }catch(SQLException se2){
			      }
			      try{
			        if(conn!=null)
			          conn.close();
			      }catch(SQLException se){
			        se.printStackTrace();
			      }
			    }
			
	}

}
