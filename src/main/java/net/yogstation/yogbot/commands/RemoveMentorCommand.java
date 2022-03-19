package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveMentorCommand extends EditRankCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, String.format("Correct usage: `%sremovementor <ckey or @Username>`", Yogbot.config.discordConfig.commandPrefix));
		try (
				Connection connection = Yogbot.database.getConnection();
				PreparedStatement mentorSetStatement = connection.prepareStatement(String.format(
						"DELETE FROM `%s` WHERE `ckey` = ?;", Yogbot.database.prefix("mentor")))
				){
			return removeRank(event, target, mentorSetStatement, Yogbot.config.discordConfig.mentorRole);
		} catch (SQLException e) {
			LOGGER.error("Error in AddMentorCommand", e);
			return reply(event, "Unable to access database.");
		}
	}

	@Override
	public String getName() {
		return "removementor";
	}

	@Override
	protected String getRequiredPermissions() {
		return "removementor";
	}

	@Override
	protected String getDescription() {
		return "Removes a user's Mentor rank";
	}
}
