package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddAOCommand extends EditRankCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, "Correct usage: `%saddao <ckey or @Username>`", Yogbot.config.discordConfig.commandPrefix);

		try (
				Connection connection = Yogbot.database.getConnection();
				PreparedStatement adminCheckStmt = connection.prepareStatement(String.format(
						"SELECT ckey FROM `%s` WHERE `ckey` = ?;", Yogbot.database.prefix("admin")));
				PreparedStatement adminSetStmt = connection.prepareStatement(String.format(
						"INSERT INTO `%s` (`ckey`, `rank`) VALUES (?, 'Admin Observer');", Yogbot.database.prefix("admin")))
				){
			return giveRank(event, target, adminCheckStmt, adminSetStmt, Yogbot.config.discordConfig.aoRole);
		} catch (SQLException e) {
			LOGGER.error("Error in AddAOCommand", e);
			return reply(event, "Unable to access database.");
		}
	}

	@Override
	public String getName() {
		return "addao";
	}

	@Override
	protected String getRequiredPermissions() {
		return "addao";
	}

	@Override
	protected String getDescription() {
		return "Give a user AO rank.";
	}
}
