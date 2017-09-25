package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	private final String FILENAME = "/static/database.txt";
	@Override
	String search(String text) throws Exception {
		//Write your code here
		String result=null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		int hits=0;
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT response, hits "
					+ "FROM responsetable WHERE keyword LIKE concat('%', ?, '%')");
			stmt.setString(1, text);
			ResultSet rs =stmt.executeQuery();
			
			
			
			while(rs.next()) {
				result=rs.getString(1);
				hits=rs.getInt(2);
			}
			
			if(result!=null) {
				PreparedStatement stmt2 = connection.prepareStatement("UPDATE responsetable "
						+ "SET hits=? WHERE keyword LIKE concat('%',?,'%')");
			    stmt2.setInt(1, hits+1);
			    stmt2.setString(2, text);
			    stmt2.executeUpdate();
			    
				return result;
			}
			
			
			rs.close();
			stmt.close();
			connection.close();
						

			
			isr = new InputStreamReader(
                    this.getClass().getResourceAsStream(FILENAME));
			br = new BufferedReader(isr);
			String sCurrentLine;
			
			while (result == null && (sCurrentLine = br.readLine()) != null) {
				String[] parts = sCurrentLine.split(":");
				
				if (text.toLowerCase().contains(parts[0].toLowerCase())) {
					result = parts[1];
					return result;
				}
				
			}
			
						
			
			
			
			
			
		}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		}finally {
			try {
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
			} catch (IOException ex) {
				log.info("IOException while closing file: {}", ex.toString());
			}
		}
		
		throw new Exception("NOT FOUND");
		//return null;
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
