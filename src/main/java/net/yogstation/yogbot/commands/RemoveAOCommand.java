package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveAOCommand extends EditRankCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, "Correct usage: `%sremoveao <ckey or @Username>`", Yogbot.config.discordConfig.commandPrefix);

		try (
				Connection connection = Yogbot.database.getConnection();
				PreparedStatement adminSetStmt = connection.prepareStatement(String.format(
						"DELETE FROM `%s` WHERE `ckey` = ?;", Yogbot.database.prefix("admin")))
				){
			return removeRank(event, target, adminSetStmt, Yogbot.config.discordConfig.aoRole);
		} catch (SQLException e) {
			LOGGER.error("Error in AddAOCommand", e);
			return reply(event, "Unable to access database.");
		}
	}

	@Override
	public String getName() {
		return "removeao";
	}

	@Override
	protected String getRequiredPermissions() {
		return "removeao";
	}

	@Override
	protected String getDescription() {
		return "Remove a user's AO rank.";
	}
}
