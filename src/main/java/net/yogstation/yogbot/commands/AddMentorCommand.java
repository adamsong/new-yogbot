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
public class AddMentorCommand extends EditRankCommand {
	
	public AddMentorCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions, database);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, "Correct usage: `%saddmentor <ckey or @Username>`", discordConfig.commandPrefix);
		try (
				Connection connection = database.getConnection();
				PreparedStatement mentorCheckStmt = connection.prepareStatement(String.format(
						"SELECT ckey FROM `%s` WHERE `ckey` = ?;", database.prefix("mentor")));
				PreparedStatement mentorSetStatement = connection.prepareStatement(String.format(
						"INSERT INTO `%s` (`ckey`, `position`) VALUES (?, 'Mentor');", database.prefix("mentor")))
				){
			return giveRank(event, target, mentorCheckStmt, mentorSetStatement, discordConfig.mentorRole);
		} catch (SQLException e) {
			LOGGER.error("Error in AddMentorCommand", e);
			return reply(event, "Unable to access database.");
		}
	}

	@Override
	public String getName() {
		return "addmentor";
	}

	@Override
	protected String getRequiredPermissions() {
		return "addmentor";
	}

	@Override
	protected String getDescription() {
		return "Gives a user Mentor rank";
	}
}
