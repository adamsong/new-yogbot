package net.yogstation.yogbot

import net.yogstation.yogbot.config.DatabaseConfig
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource
import org.springframework.stereotype.Component
import java.sql.Connection
import kotlin.Throws
import java.sql.SQLException

@Component
class DatabaseManager(private val config: DatabaseConfig) {
	private val ds: MysqlConnectionPoolDataSource = MysqlConnectionPoolDataSource()

	init {
		ds.serverName = config.hostname
		ds.port = config.port
		ds.databaseName = config.database
		ds.user = config.username
		ds.password = config.password
	}

	fun prefix(tableName: String): String {
		return config.prefix + tableName
	}

	@get:Throws(SQLException::class)
	val connection: Connection
		get() = ds.connection
}
