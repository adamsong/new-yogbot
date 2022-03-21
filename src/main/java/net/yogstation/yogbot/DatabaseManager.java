package net.yogstation.yogbot;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import net.yogstation.yogbot.config.DatabaseConfig;
import org.springframework.stereotype.Component;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseManager {
	private final DatabaseConfig config;
	private final MysqlConnectionPoolDataSource ds;

	public DatabaseManager(DatabaseConfig config) throws SQLException {
		this.config = config;
		ds = new MysqlConnectionPoolDataSource();
		ds.setServerName(config.hostname);
		ds.setPort(config.port);
		ds.setDatabaseName(config.database);
		ds.setUser(config.username);
		ds.setPassword(config.password);
	}

	public String prefix(String tableName) {
		return config.prefix + tableName;
	}

	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}
