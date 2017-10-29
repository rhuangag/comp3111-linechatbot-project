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
	
	//TODO
	//Search suitable tours in the database with the keyword and return an output in text format
	//This also need a chasing state in the message class since after filter will directly changed to booking
	//Current version we don't consider fail filting result
	public String filterSearch(String keyword) {
		String result=null;
		try {
			int orderNumber=1;
			Connection connection = KitchenSinkController.getConnection();
			PreparedStatement filterStmt = connection.prepareStatement
					("SELECT TourID, TourName from TourList where TourDescription like cancat('%', ?, '%') or TourID like ('%', ?, '%')");
			filterStmt.setString(1, keyword);
			filterStmt.setString(2, keyword);
			ResultSet rsForFilter = filterStmt.executeQuery();
			
			result="Yes.We have those similar tours+\n";
			while(rsForFilter.next()) {
				PreparedStatement updateTemporaryFilterTable = connection.prepareStatement("INSERT into TemporaryFilterTable VALUES ("+orderNumber+", "+rsForFilter.getString("TourID")+");");
				updateTemporaryFilterTable.executeUpdate();
				result+=orderNumber+". "+rsForFilter.getString("TourID")+ " "+rsForFilter.getString("TourName")+"\n";
				orderNumber++;
				updateTemporaryFilterTable.close();
			}
			
			PreparedStatement clearTempTable =connection.prepareStatement("Delete from TemporaryFilterTable;");
			clearTempTable.executeUpdate();
			
			clearTempTable.close();
			rsForFilter.close();
			filterStmt.close();
			connection.close();
		} catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
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
				("SELECT TourID from TemporarayFilterTalbe where Order like cancat('%', ?, '%')");
		filterFromTemTable.setString(1, keyword);
		ResultSet rsForOrder = filterFromTemTable.executeQuery();
		while(rsForOrder.next()) {
			TourID=rsForOrder.getString("TourID");
		}
		//SELECT detials of the trip
		//HERE NEED to update since need to show confirmed trip and those still accept application
		PreparedStatement detailStmt = connection.prepareStatement
				("SELECT TourID, TourName, TourDescrption, Date, WeekendPrice, WeekdayPrice from TourList where TourID "
						+ "like cancat('%', ?, '%')");
		detailStmt.setString(1, TourID);
		ResultSet detialRs=detailStmt.executeQuery();
		while(detialRs.next()){
			result=detialRs.getString("TourID")+ " "+detialRs.getString("TourName")+"* "+detialRs.getString("TourDescription")+". " + "We have confirmed tour on"/*here need update and fix for confirmed tour etc.*/+" "+
					"We have tour on "/*Here need to fixed for accept application tours etc.*/ +" Fee: "+detialRs.getString("Price")+"Do you want to book this one? \n";
		}
		rsForOrder.close();
		filterFromTemTable.close();
		detailStmt.close();
		detialRs.close();
		connection.close();
	} catch (Exception e){
			log.info("Exception while reading database: {}", e.toString());
	}
		return result;
	}
}
	
	