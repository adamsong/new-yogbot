package net.yogstation.yogbot.config;

import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig extends ConfigClass {
	// Connection Info, default matches the game server's default
	public String hostname = "localhost";
	public int port = 3306;
	public String database = "feedback";
	public String username = "username";
	public String password = "password";

	public String prefix = "SS13_";
	
	public DatabaseConfig() {
		loadConfig("database.properties");
	}
}
