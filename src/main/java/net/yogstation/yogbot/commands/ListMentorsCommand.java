package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListMentorsCommand extends TextCommand {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		try (Connection connection = Yogbot.database.getConnection();
			 PreparedStatement stmt = connection.prepareStatement(
				 String.format("SELECT ckey FROM `%s`", Yogbot.database.prefix("mentor"))
			 )
		) {
			ResultSet results = stmt.executeQuery();
			StringBuilder builder = new StringBuilder("Current Mentors:");
			while(results.next()) {
				builder.append("\n");
				builder.append(results.getString("ckey"));
			}
			results.close();
			return reply(event, builder.toString());
		} catch (SQLException e) {
			LOGGER.error("Error with SQL Query", e);
			return reply(event, "An error has occurred");
		}
	}

	@Override
	protected String getDescription() {
		return "Get current mentors.";
	}

	@Override
	public String getName() {
		return "listmentors";
	}
}
