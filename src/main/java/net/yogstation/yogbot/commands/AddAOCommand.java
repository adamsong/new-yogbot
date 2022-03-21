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
public class AddAOCommand extends EditRankCommand {
	
	public AddAOCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions, database);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, "Correct usage: `%saddao <ckey or @Username>`", discordConfig.commandPrefix);

		try (
				Connection connection = database.getConnection();
				PreparedStatement adminCheckStmt = connection.prepareStatement(String.format(
						"SELECT ckey FROM `%s` WHERE `ckey` = ?;", database.prefix("admin")));
				PreparedStatement adminSetStmt = connection.prepareStatement(String.format(
						"INSERT INTO `%s` (`ckey`, `rank`) VALUES (?, 'Admin Observer');", database.prefix("admin")))
				){
			return giveRank(event, target, adminCheckStmt, adminSetStmt, discordConfig.aoRole);
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
