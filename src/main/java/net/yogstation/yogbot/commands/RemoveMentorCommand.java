package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class RemoveMentorCommand extends EditRankCommand {
	
	public RemoveMentorCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions, database);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, "Correct usage: `%sremovementor <ckey or @Username>`", discordConfig.commandPrefix);
		try (
				Connection connection = database.getConnection();
				PreparedStatement mentorSetStatement = connection.prepareStatement(String.format(
						"DELETE FROM `%s` WHERE `ckey` = ?;", database.prefix("mentor")))
				){
			return removeRank(event, target, mentorSetStatement, discordConfig.mentorRole);
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
