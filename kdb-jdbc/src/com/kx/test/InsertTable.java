package com.kx.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class InsertTable {
	
	static String DRIVER; // driver name
	static String URL; // connection argument format for q process
	static String USER; // user name
	static String PASSWORD; // password

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
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
	    PreparedStatement prepStmt = null;

	    try{
	      Class.forName(DRIVER);

	      System.out.println("Connecting to " + URL.substring(7) + "...");
	      conn=DriverManager.getConnection(URL,USER,PASSWORD); // put the connection information
	      System.out.println("Connected database successfully...");

	      System.out.println("Creating statement...");
          stmt = conn.createStatement(); // Ready for the sql statement

          String sql = "CREATE TABLE trade (sym varchar(255), ask float, bid float)";
  	  	  //!!!! Before this statement is executed, s.k. should be loaded in q process.
	      stmt.executeUpdate(sql);
          sql = "insert into trade (sym,ask,bid) values ('aaa', 1.1, 1.5)";
	      stmt.executeUpdate(sql);
          sql = "insert into trade (sym,ask,bid) values ('bbb', 0.01, 0.02)";
	      stmt.executeUpdate(sql);
	      sql = "insert into trade (sym,ask,bid) values ('ccc',10.0, 12.1)";
	      stmt.executeUpdate(sql);
	      sql = "insert into trade (sym,ask,bid) values ('aaa', 1.15, 1.45)";
	      stmt.executeUpdate(sql);
	      stmt.close();

	      conn.close();
	      System.out.println("Connection was closed...");

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
