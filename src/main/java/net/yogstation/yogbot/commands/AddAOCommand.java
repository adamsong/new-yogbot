package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

public class AddAOCommand extends AddRankCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if(args.length < 2) {
			assert Yogbot.config.discordConfig != null;
			return reply(event, String.format("Correct usage: `%saddao <ckey>`", Yogbot.config.discordConfig.commandPrefix));
		}

		String ckey = args[1].toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
		try (
				Connection connection = Yogbot.database.getConnection();
				PreparedStatement playerStmt = connection.prepareStatement(String.format(
						"SELECT discord_id FROM `%s` WHERE `ckey` = ? AND discord_id IS NOT NULL;", Yogbot.database.prefix("player")));
				PreparedStatement adminCheckStmt = connection.prepareStatement(String.format(
						"SELECT ckey FROM `%s` WHERE `ckey` = ?;", Yogbot.database.prefix("admin")));
				PreparedStatement adminSetStmt = connection.prepareStatement(String.format(
						"INSERT INTO `%s` (`ckey`, `rank`) VALUES (?, 'Admin Observer');", Yogbot.database.prefix("admin")))
				){
			assert Yogbot.config.discordConfig != null;
			return giveRank(event, ckey, playerStmt, adminCheckStmt, adminSetStmt, Yogbot.config.discordConfig.aoRole);
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
