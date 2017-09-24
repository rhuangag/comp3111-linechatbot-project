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
		try {
			isr = new InputStreamReader(
                    this.getClass().getResourceAsStream(FILENAME));
			br = new BufferedReader(isr);
			String sCurrentLine;
			
			while (result == null && (sCurrentLine = br.readLine()) != null) {
				String[] parts = sCurrentLine.split(":");
				
				if (text.toLowerCase().contains(parts[0].toLowerCase()))
					result = parts[1];
			}
			
			
			//String username="programmer";
			//String password ="961210";
			/*String dbUrl= "postgres://"
					+ "inkuswftzxmrsq:"
					+ "103f05a579ba5d5a4796a9743f8c4b03452f280e3194789ccf89f48bcbeee0ce@ec2"
					+ "-50-19-105-113."
					+ "compute-1.amazonaws.com:5432/d81l6qfelu4n3l";*/
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT response "
					+ "FROM responsetable where keyword like concat('%', ?, '%')");
			stmt.setString(1, text);
			ResultSet rs =stmt.executeQuery();
			
			
			
			while(rs.next()) 
				result=rs.getString(1);
				
			
			
			rs.close();
			stmt.close();
			connection.close();
			
			
			
			
		}catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		} 		
		
		if (result != null)
			return result;
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
