package com.example.bot.spring;


import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.math.BigDecimal;
import java.math.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import java.io.OutputStream;
import java.io.BufferedReader; 
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
/**
 * 
 * The class Report can create and write a txt file which includes the useful questions and feedback from the customers.
 *
 */
public class Report {
	private String dbname;
	
	/**
	 * Constructor of Report. It initializes the object with the date of the report output and the database name(question or feedback) for output.
	 * @param date This is the date when the file is created.
	 * @param dbName This is the type of report that are going to be generated.
	 */
	public Report(String dbName) {
		this.dbname = dbName;
	}
	/**
	 * This method can create a file with the file name provided.
	 * @param filename This is the name of the file that is going to be created.
	 * @return Boolean This returns whether the file is succesfully created.
	 * @throws Exception
	 */
	public boolean createFile(File filename)throws Exception{
		boolean flag=false;
		try{
			if(!filename.exists()){
				filename.createNewFile();
				flag=true;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		return true;
	}
	public boolean writeTxtFile(String content,File fileName)throws Exception{
		RandomAccessFile mm=null;
		boolean flag=false;
		FileOutputStream o=null;
		try {
			o = new FileOutputStream(fileName);
			o.write(content.getBytes("GBK"));
			o.close();
 			mm=new RandomAccessFile(fileName,"rw");
			mm.writeBytes(content);
			flag=true;
		} catch (Exception e) {
//	handle exception
			e.printStackTrace();
		}finally{
			if(mm!=null){
				mm.close();
				}
			}
		return flag;
		}
	
	/**
	 * This method can read the database and write the output file with the data.
	 * @return java.lang.String This returns a message "built!" when the report is written correctly, and returns the error message when an Exception is thrown.
	 */
	public String writeReport(){
		try {
			String name = this.dbname;
			ByteArrayInputStream stream = new ByteArrayInputStream(name.getBytes());
			File filename = null;
			OutputStream os = new FileOutputStream(filename);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead= stream.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			stream.close();
			boolean createR = this.createFile(filename);
			String fulltext = null;
			if (this.dbname == "usefulquestionrecord")
				fulltext = "type integer  customerID                   usefulquestion\n";
			if (this.dbname == "feedbacktable")
				fulltext = "tourID  userID                      feedback\n";
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement read = connection.prepareStatement("Select * from " + this.dbname);
			ResultSet readrs= read.executeQuery();
			if (this.dbname == "usefulquestionrecord"){
				while (readrs.next()) {
					fulltext += readrs.getInt(2);
					fulltext += "             ";
					fulltext += readrs.getString(3);
					fulltext += "  ";
					fulltext += readrs.getString(1);
					fulltext += "\n";
				}
			}
			if (this.dbname == "feedbacktable"){
				while (readrs.next()) {
					fulltext += readrs.getString(2);
					fulltext += "   ";
					fulltext += readrs.getString(1);
					fulltext += "  ";
					fulltext += readrs.getString(3);
					fulltext += "\n";
				}
			}
			read.close();
			boolean writeR = this.writeTxtFile(fulltext, filename);
			connection.close();
			return "Built!";
    	}catch (Exception e){
			//log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"report");}
	}
}