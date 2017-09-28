package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	private static boolean LOCAL=false;
	@Override
	String search(String text) throws Exception {
		//Write your code here
		String result;
		Connection connection=getConnection();
		
		PreparedStatement stmt=connection.prepareStatement("SELECT response FROM chatbotDBTable "
				+ "WHERE LOWER(request) LIKE LOWER( concat('%',?,'%') )");
		
		stmt.setString(1,text);
		
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {	// not found
			rs.close();
			stmt.close();
			connection.close();
			throw new Exception("NOT FOUND");
		}else {				//found
			result=rs.getString(1);
		}
		rs.close();
		stmt.close();
		connection.close();
		return result;
	}
	
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username;
		String password;
		String dbUrl;
		
			username = dbUri.getUserInfo().split(":")[0];
			password = dbUri.getUserInfo().split(":")[1];
			dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
	
		
//		String username = dbUri.getUserInfo().split(":")[0];
//		String password = dbUri.getUserInfo().split(":")[1];
//		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		
		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
