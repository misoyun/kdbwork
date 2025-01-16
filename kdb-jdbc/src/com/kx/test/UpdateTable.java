package com.kx.test;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class UpdateTable {
	
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

			     //Call function to insert the table
			      
			     stmt = conn.createStatement();

			  	 String sql = "update trade set ask=0.5 where sym='aaa'";
			  	 stmt.execute(sql);
			     stmt.close();

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
