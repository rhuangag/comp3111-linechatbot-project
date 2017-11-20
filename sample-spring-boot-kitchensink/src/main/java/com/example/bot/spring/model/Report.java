package com.example.bot.spring;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.PushMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;
import java.time.LocalDateTime;
import java.time.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.time.format.DateTimeFormatter;

import java.util.Observable;

import lombok.extern.slf4j.Slf4j;

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
 * This class is involved in mediator pattern: mediator TextHandler will check the type of input if customer is the employee that activated client channel (e.g. input the correct password)
 * If the employee ask for report of useful question record, TextHandler will push the recorded questions.
 */
public class Report {
	private String dbname;
	private Customer customerbelonging;
	
	/**
	 * Constructor of Report. It initializes the object with the date of the report output and the database name(question or feedback) for output.
	 * @param date This is the date when the file is created.
	 * @param dbName This is the type of report that are going to be generated.
	 */
	public Report(String dbName, Customer c) {
		this.dbname = dbName;
		this.customerbelonging = c;
	}

	/*
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
	*/
	/**
	 * This method can read the database and generate output message with the data.
	 * @return java.lang.String This returns a message contains the question records and feedback record.
	 */
	public String writeReport(){
		try {
			/*String name = this.dbname;
			ByteArrayInputStream stream = new ByteArrayInputStream(name.getBytes());
			File filename = null;
			OutputStream os = new FileOutputStream(filename);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead= stream.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			stream.close();*/
			//boolean createR = this.createFile(filename);
			String fulltext = null;
			
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement read = connection.prepareStatement("Select * from " + this.dbname);
			ResultSet readrs= read.executeQuery();
			if (this.dbname == "usefulquestionrecord") {
				//fulltext = "type integer  customerID                             usefulquestion\n";
				fulltext = "usefulquestion\n";

				int count = 0;
				while (readrs.next()) {
					count++;
					/* fulltext += readrs.getInt(2);
					fulltext += "             ";
					fulltext += readrs.getString(3);
					fulltext += "  "; */
					fulltext += readrs.getString(1);
					fulltext += "\n";
					if(count == 10) {
						TextMessage textMessage = new TextMessage(fulltext);
						PushMessage pushMessage = new PushMessage(this.customerbelonging.getID(), textMessage);
						KitchenSinkController.pushMessageController(pushMessage);
						count = 0;
						fulltext = "";
					}
				}
				if (fulltext != "") {
						TextMessage textMessage = new TextMessage(fulltext);
						PushMessage pushMessage = new PushMessage(this.customerbelonging.getID(), textMessage);
						KitchenSinkController.pushMessageController(pushMessage);
				}
			}
			else if (this.dbname == "feedbacktable") {
				//fulltext = "tourID   userID                          feedback\n";
				fulltext = "tourID        feedback\n";

				int count =0;
				while (readrs.next()) {
					count++;
					fulltext += readrs.getString(2);
					fulltext += "      ";
					/*fulltext += readrs.getString(1);
					fulltext += "  "; */
					fulltext += readrs.getString(3);
					fulltext += "\n";
					if(count == 10) {
						TextMessage textMessage = new TextMessage(fulltext);
						PushMessage pushMessage = new PushMessage(this.customerbelonging.getID(), textMessage);
						KitchenSinkController.pushMessageController(pushMessage);
						count = 0;
						fulltext = "";
					}
				}
				if (fulltext != "") {
					TextMessage textMessage = new TextMessage(fulltext);
					PushMessage pushMessage = new PushMessage(this.customerbelonging.getID(), textMessage);
					KitchenSinkController.pushMessageController(pushMessage);
				}	
			}
			readrs.close();
			read.close();
			//boolean writeR = this.writeTxtFile(fulltext, filename);
			connection.close();
			return "123";
    	}catch (Exception e){
			//log.info("Exception while reading database: {}", e.toString());
			return (e.toString()+"report");}
	}
}