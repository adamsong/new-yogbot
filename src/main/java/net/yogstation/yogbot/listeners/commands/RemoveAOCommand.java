package net.yogstation.yogbot.listeners.commands;

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
public class RemoveAOCommand extends EditRankCommand {
	
	public RemoveAOCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions, database);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		if(target == null)
			return reply(event, "Correct usage: `%sremoveao <ckey or @Username>`", discordConfig.commandPrefix);

		try (
				Connection connection = database.getConnection();
				PreparedStatement adminSetStmt = connection.prepareStatement(String.format(
						"DELETE FROM `%s` WHERE `ckey` = ?;", database.prefix("admin")))
				){
			return removeRank(event, target, adminSetStmt, discordConfig.aoRole);
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
