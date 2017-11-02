package com.example.bot.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Filter {
	public Filter(){}
	
    //no data member for this class
	
	//Methods
	//helper function to detect if the input keyword is totally numeric
	private boolean isNumeric(String str){
		for (int i=0; i<str.length();i++){
			if (!Character.isDigit(str.charAt(i))){
					return false;
			}
		}
		return true;
	}
	////helper function to record and convert data after filtering into string
	private String prepareResultAndUpdateTempTable(ResultSet rs,Connection connection) {
		int orderNumber=1;
		String result=null;
		try {
		if(rs!=null) {
				result="Yes.We have those tours that may match your requirements:\n";
			while(rs.next()) {
				PreparedStatement updateTemporaryFilterTable = connection.prepareStatement("INSERT into TemporaryFilterTable VALUES (?,?)");
				updateTemporaryFilterTable.setString(1,rs.getString("TourID"));
				updateTemporaryFilterTable.setString(2,rs.getString("TourName"));
				updateTemporaryFilterTable.executeUpdate();
				
				result+=orderNumber+". "+rs.getString("TourID")+ " "+rs.getString("TourName")+"\n";
				orderNumber++;
				updateTemporaryFilterTable.close();
			}
		}
		else {
			result="Sorry, we cannot find any match answer for your question :( we already record your question and will forward it to the tour company.";
		}
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();
		}
		return result;
	}
		
		

	//TODO
	//Search suitable tours in the database with the keyword and return an output in text format
	//This also need a chasing state in the message class since after filter will directly changed to booking
	//Current version we don't consider fail filting result
	public String filterSearch(String keyword) {
		String result=null;
		try {
			Connection connection = KitchenSinkController.getConnection();
			
		//case 1: "I want to book a tour." expected outcome is all tours provide
		if (keyword.equals("book")) {
			PreparedStatement allListStmt= connection.prepareStatement
					("SELECT TourID, TourName from TourList");
			ResultSet rsForAllList=allListStmt.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForAllList,connection);
			allListStmt.close();
			rsForAllList.close();
		}
		
		//case 2: keyword is "tour", which is not count
		else if (keyword.equals("tour")) {
					connection.close();
					return null;
		}
		
		//case 3: filter for price range
		else if(keyword.contains(",")){
			String[] parts = keyword.split(",");
			int lowerLimitation=Integer.parseInt(parts[0]);
			int upperLimitation=Integer.parseInt(parts[1]);
			PreparedStatement filterStmtForPriceRange = connection.prepareStatement
						("SELECT TourID, TourName from TourList where ?=<cast(weekdayprice as int) and ?>=cast(weekdayprice as int)");
			filterStmtForPriceRange.setInt(1,lowerLimitation);
			filterStmtForPriceRange.setInt(2, upperLimitation);
			ResultSet rsForPriceRange=filterStmtForPriceRange.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForPriceRange,connection);
			connection.close();
			filterStmtForPriceRange.close();
			rsForPriceRange.close();
		}
		
		//case 4: filter for a price, +-100 for "around" type, >100 to distinguish from duration, current version only filt for weekday price
		else if(isNumeric(keyword)&&(Integer.parseInt(keyword)>100)) {
			 int upperLimitation=Integer.parseInt(keyword)+100;
			 int lowerLimitation=Integer.parseInt(keyword)-100;
		 PreparedStatement filterStmtForPrice = connection.prepareStatement
						("SELECT TourID, TourName from TourList where ?=<cast(weekdayprice as int) and ? >=cast(weekdayprice as int)");
		 filterStmtForPrice.setInt(1,lowerLimitation);
		 filterStmtForPrice.setInt(2,upperLimitation);
		 ResultSet rsForPrice=filterStmtForPrice.executeQuery();
		 result=prepareResultAndUpdateTempTable(rsForPrice,connection);
		 connection.close();
		 filterStmtForPrice.close();
		 rsForPrice.close();
		}
		
		//case 5:filter for higher price
		else if (keyword.contains("<")) {
			String[] parts = keyword.split("<");
			int lowerLimitation=Integer.parseInt(parts[0]);
			PreparedStatement filterStmtForHigherPrice = connection.prepareStatement
					("SELECT TourID, TourName from TourList where ?=<cast(weekdayprice as int)");
			filterStmtForHigherPrice.setInt(1,lowerLimitation);
			 ResultSet rsForHigherPrice=filterStmtForHigherPrice.executeQuery();
			 result=prepareResultAndUpdateTempTable(rsForHigherPrice,connection);
			 connection.close();
			 filterStmtForHigherPrice.close();
			 rsForHigherPrice.close();
		}
		//case 6:filter for cheaper price
		//case 6:filter for longer duration
		//case 7:filter for shorter duration
		//case 8:filter for duration
		//should we really consider such lots of cases?
		
		//Normal cases: filter for keywords in description or tour name(lcoation).
		else {
			PreparedStatement filterStmt = connection.prepareStatement
					("SELECT TourID, TourName from TourList where TourDescription like concat('%', ?, '%') or TourID like concat('%', ?, '%') or Duration like concat('%', ?, '%') ");
			filterStmt.setString(1, keyword);
			filterStmt.setString(2, keyword);
			filterStmt.setString(3, keyword);
			ResultSet rsForFilter = filterStmt.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForFilter,connection);
			rsForFilter.close();
			filterStmt.close();
		}
		connection.close();	
		}catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();
		}
		return result;
	}
	
	//TODO
	//Return the details of a tour from the database with the keyword
	//also need a tracing status for this step since will turn to start of booking
	//current version not showing the confimed or those still accept booking trips
	public String viewDetails(String keyword) {
		String result=null;
		String TourID=null;
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement filterFromTemTable = connection.prepareStatement
				("SELECT TourID from TemporarayFilterTalbe where OrderNumber like concat('%', ?, '%')");
		filterFromTemTable.setString(1, keyword);
		ResultSet rsForOrder = filterFromTemTable.executeQuery();
		while(rsForOrder.next()) {
			TourID=rsForOrder.getString("TourID");
		}
		//SELECT detials of the trip
		//HERE NEED to update since need to show confirmed trip and those still accept application
		PreparedStatement detailStmt = connection.prepareStatement
				("SELECT TourID, TourName, TourDescrption, Date, WeekendPrice, WeekdayPrice from TourList where TourID "
						+ "like concat('%', ?, '%')");
		detailStmt.setString(1, TourID);
		ResultSet detialRs=detailStmt.executeQuery();
		while(detialRs.next()){
			result=detialRs.getString("TourID")+ " "+detialRs.getString("TourName")+"* "+detialRs.getString("TourDescription")+". " + "We have confirmed tour on"/*here need update and fix for confirmed tour etc.*/+" "+
					"We have tour on "/*Here need to fixed for accept application tours etc.*/ +" Fee: Weekend "+detialRs.getInt("WeekendPrice")+"Weekday: "+ detialRs.getInt("WeekdayPrice")+". Do you want to book this one? \n";
		}
		
		//clear Temporary Filter Table after used
		PreparedStatement clearTempTable =connection.prepareStatement("Delete from TemporaryFilterTable;");
		clearTempTable.executeUpdate();
		clearTempTable.close();
		rsForOrder.close();
		filterFromTemTable.close();
		detailStmt.close();
		detialRs.close();
		connection.close();
	
	} catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
			return e.toString();
	}
		return result;
	}
}

	
	