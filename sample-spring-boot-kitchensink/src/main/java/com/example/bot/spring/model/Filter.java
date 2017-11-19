package com.example.bot.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.extern.slf4j.Slf4j;

/**
 * The class Filter can search the tours which fulfill the criteria provided by the users. 
 * It can also show the details of a tour to the user.
 * 
 *
 */
@Slf4j
public class Filter {
	private String userID;
	
	/**
	 * Constructor of the class Filter. It requires the user id of the customer.
	 * @param ID This is the user id of the customer
	 */
	public Filter(String ID){
		userID=ID;
	}
	
	
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
		if(rs.next()) {
				result="Yes. We have those similar tours that may match your requirements:\n";
				PreparedStatement updateTemporaryFilterTable = connection.prepareStatement("INSERT into TemporaryFilterTable VALUES (?,?,?)");
				updateTemporaryFilterTable.setInt(1,orderNumber);
				updateTemporaryFilterTable.setString(2,rs.getString("TourID"));
				updateTemporaryFilterTable.setString(3,userID);
				updateTemporaryFilterTable.executeUpdate();
				
				result+=orderNumber+". "+rs.getString("TourID")+ " "+rs.getString("TourName")+"\n";
				orderNumber++;
				updateTemporaryFilterTable.close();
				
			while(rs.next()) {
				updateTemporaryFilterTable = connection.prepareStatement("INSERT into TemporaryFilterTable VALUES (?,?,?)");
				updateTemporaryFilterTable.setInt(1,orderNumber);
				updateTemporaryFilterTable.setString(2,rs.getString("TourID"));
				updateTemporaryFilterTable.setString(3,userID);
				updateTemporaryFilterTable.executeUpdate();
				
				result+=orderNumber+". "+rs.getString("TourID")+ " "+rs.getString("TourName")+"\n";
				orderNumber++;
				updateTemporaryFilterTable.close();
			}
			result+="\nPlease select one of the number to view details if you are interested";
		}
		else {
			result="Sorry, we cannot find any match answer for your question :( We already record your question and will forward it to the tour company.";
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
	/**
	 * This method can take the searching keyword from customer, find tours fulfill the criteria and return the list of the tours.
	 * @param keyword This is the searching keyword from the class TextHandler
	 * @return java.lang.String This returns all possible tours information that may fulfill the requirement from customer base on different type of keywords
	 */
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
						("SELECT TourID, TourName from TourList where ?<cast(weekdayprice as int) and ?>cast(weekdayprice as int)");
			filterStmtForPriceRange.setInt(1,lowerLimitation);
			filterStmtForPriceRange.setInt(2, upperLimitation);
			ResultSet rsForPriceRange=filterStmtForPriceRange.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForPriceRange,connection);
			connection.close();
			filterStmtForPriceRange.close();
			rsForPriceRange.close();
		}
		
		//case 4: filter for a price, +-100 for "around" type, >50 to distinguish from duration, current version only filt for weekday price
		// or filter for a duration, for "around" type, <=50, need perfectly match
		else if(isNumeric(keyword)) {
			int number = Integer.parseInt(keyword);
			// price
			if(number>50 ) {
			 int upperLimitation=number+50;
			 int lowerLimitation=number-50;
		 PreparedStatement filterStmtForPrice = connection.prepareStatement
						("SELECT TourID, TourName from TourList where ?<=cast(weekdayprice as int) and ? >=cast(weekdayprice as int)");
		 filterStmtForPrice.setInt(1,lowerLimitation);
		 filterStmtForPrice.setInt(2,upperLimitation);
		 ResultSet rsForPrice=filterStmtForPrice.executeQuery();
		 result=prepareResultAndUpdateTempTable(rsForPrice,connection);
		 connection.close();
		 filterStmtForPrice.close();
		 rsForPrice.close();
		}
			//duration
		else {
			PreparedStatement filterStmtForDuration = connection.prepareStatement
				("SELECT TourID, TourName from TourList where ? =cast(Duration as int)");
			filterStmtForDuration.setInt(1, number);
			ResultSet rsForDuration=filterStmtForDuration.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForDuration,connection);
			connection.close();
			filterStmtForDuration.close();
			rsForDuration.close();
			
			}
		}
		
		//case 5:filter for higher price or longer duration
		else if (keyword.contains(">")) {
			String[] parts = keyword.split(">");
			int lowerLimitation=Integer.parseInt(parts[1]);
		//higher price
		if(lowerLimitation>=50) {
			PreparedStatement filterStmtForHigherPrice = connection.prepareStatement
					("SELECT TourID, TourName from TourList where ?<cast(weekdayprice as int)");
			filterStmtForHigherPrice.setInt(1,lowerLimitation);
			ResultSet rsForHigherPrice=filterStmtForHigherPrice.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForHigherPrice,connection);
			connection.close();
			filterStmtForHigherPrice.close();
			rsForHigherPrice.close();
		}
		//longer duration
		else {
			PreparedStatement filterStmtForLongerDuration = connection.prepareStatement
					("SELECT TourID, TourName from TourList where ?<cast(Duration as int)");
			filterStmtForLongerDuration.setInt(1,lowerLimitation);
			ResultSet rsForLongerDuration=filterStmtForLongerDuration.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForLongerDuration,connection);
			connection.close();
			filterStmtForLongerDuration.close();
			rsForLongerDuration.close();
		}
		}
		//case 6:filter for cheaper price or shorter duration
		else if (keyword.contains("<")) {
			String[] parts = keyword.split("<");
			int lowerLimitation=Integer.parseInt(parts[1]);
			//cheaper price
			if (lowerLimitation>=50) {
			PreparedStatement filterStmtForCheaperPrice = connection.prepareStatement
					("SELECT TourID, TourName from TourList where ?>cast(weekdayprice as int)");
			filterStmtForCheaperPrice.setInt(1,lowerLimitation);
			 ResultSet rsForCheaperPrice=filterStmtForCheaperPrice.executeQuery();
			 result=prepareResultAndUpdateTempTable(rsForCheaperPrice,connection);
			 connection.close();
			 filterStmtForCheaperPrice.close();
			 rsForCheaperPrice.close();
			}
			 //shorter duration
			 else {
			PreparedStatement filterStmtForShorterDuration = connection.prepareStatement
					("SELECT TourID, TourName from TourList where ?>cast(Duration as int)");
			filterStmtForShorterDuration.setInt(1,lowerLimitation);
			ResultSet rsForShorterDuration=filterStmtForShorterDuration.executeQuery();
			result=prepareResultAndUpdateTempTable(rsForShorterDuration,connection);
			connection.close();
			filterStmtForShorterDuration.close();
			rsForShorterDuration.close(); 
			}
		}
		//should we really consider such lots of cases?
		
		//Normal cases: filter for keywords in description or tour name(lcoation).
		else {
			PreparedStatement filterStmt = connection.prepareStatement
					("SELECT TourID, TourName from TourList where lower(TourDescription) like concat('%', ?, '%') or TourID like concat('%', ?, '%') or lower(TourName) like concat('%', ?, '%') or lower(Date) like concat('%', ?, '%')");
			filterStmt.setString(1, keyword);
			filterStmt.setString(2, keyword);
			filterStmt.setString(3, keyword);
			filterStmt.setString(4, keyword);
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
	/**
	 * This method is used to display the details of the tour which is selected by the customer.
	 * @param keyword This is the customer's selection from the tour list 
	 * @return java.lang.String This returns the details of the tour selected by customer and ask if customer want to book this tour. If the keyword is not in the list provided previously, this returns reminder information that customer make a wrong choice
	 */
	public String viewDetails(String keyword) {
		String result=null;
		String TourID=null;
		
		try {
		Connection connection = KitchenSinkController.getConnection();
		PreparedStatement clearTempTable =connection.prepareStatement("Delete from TemporaryFilterTable where userId =?");
		PreparedStatement filterFromTemTable = connection.prepareStatement
				("SELECT TourID from TemporaryFilterTable where OrderNumber =? and UserId LIKE concat('%', ?, '%')");
		filterFromTemTable.setString(1, keyword);
		filterFromTemTable.setString(2, userID);
		
		ResultSet rsForOrder = filterFromTemTable.executeQuery();
		if(rsForOrder.next()) {
			TourID=rsForOrder.getString("TourID");
		}
		else {
			
			
			filterFromTemTable.close();
			rsForOrder.close();
			clearTempTable.setString(1, userID);
			clearTempTable.executeUpdate();
			clearTempTable.close();
			connection.close();
			return "Sorry that there is no such a choice. You may ask for specific tours again and please show me the coorect choice :)";
		}
		//SELECT detials of the trip
		PreparedStatement findAvailiable = connection.prepareStatement
				("SELECT  departuredate from bookingtable where status='availiable' and tourID =?");
		findAvailiable.setString(1, TourID);
		ResultSet rsFindAvailiable=findAvailiable.executeQuery();
		String availiable="";
		while(rsFindAvailiable.next()) { 
		 availiable+=rsFindAvailiable.getString(1)+ "\n";
		}
		
		PreparedStatement findConfirmedButAcceptBooking = connection.prepareStatement
				("SELECT  departuredate from bookingtable where status='confrimed' and tourID =?");
		findConfirmedButAcceptBooking.setString(1, TourID);
		ResultSet rsFindConfirmedButAcceptBooking=findConfirmedButAcceptBooking.executeQuery();
		String confirmedButAcceptBook="";
		while(rsFindConfirmedButAcceptBooking.next()) { 
		confirmedButAcceptBook+=rsFindConfirmedButAcceptBooking.getString(1)+ "\n";
		}
		
		
		
		PreparedStatement detailStmt = connection.prepareStatement
				("SELECT TourID, TourName, TourDescription, Date, WeekendPrice, WeekdayPrice from TourList where TourID "
						+ "like concat('%', ?, '%')");
		detailStmt.setString(1, TourID);
		ResultSet detialRs=detailStmt.executeQuery();
		while(detialRs.next()){
			result=detialRs.getString("TourID")+ " "+detialRs.getString("TourName")+"* "+detialRs.getString("TourDescription")+". " + "\nWe have confirmed tour on：\n"+confirmedButAcceptBook+
					"We have tour on these days still accept application:\n"+availiable+"\nFee: Weekend HKD"+detialRs.getInt("WeekendPrice")+" Weekday HKD "+ detialRs.getInt("WeekdayPrice")+".\nDo you want to book this one? \n";
		}
		
		//clear Temporary Filter Table after used
	
		clearTempTable.setString(1, userID);
		clearTempTable.executeUpdate();
		clearTempTable.close();
		findConfirmedButAcceptBooking.close();
		rsFindConfirmedButAcceptBooking.close();
		findAvailiable.close();
		rsFindAvailiable.close();
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

	
	