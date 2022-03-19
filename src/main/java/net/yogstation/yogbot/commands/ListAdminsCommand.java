package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.sql.*;

public class ListAdminsCommand extends TextCommand {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		try (Connection connection = Yogbot.database.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(
				 String.format("SELECT ckey,`rank` FROM `%s`", Yogbot.database.prefix("admin"))
			 )
		) {
			ResultSet results = stmt.executeQuery();
			StringBuilder builder = new StringBuilder("Current Admins:");
			while(results.next()) {
				builder.append("\n");
				builder.append(results.getString("ckey"));
				builder.append(" - ");
				builder.append(results.getString("rank"));
			}
			return reply(event, builder.toString());
		} catch (SQLException e) {
			LOGGER.error("Error with SQL Query", e);
			return reply(event, "An error has occurred");
		}
	}

	@Override
	protected String getDescription() {
		return "Get current admins.";
	}

	@Override
	public String getName() {
		return "listadmins";
	}
}
