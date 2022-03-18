package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

public class AddMentorCommand extends AddRankCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		String[] args = event.getMessage().getContent().split(" ");
		if(args.length < 2) {
			assert Yogbot.config.discordConfig != null;
			return reply(event, String.format("Correct usage: `%saddmentor <ckey>`", Yogbot.config.discordConfig.commandPrefix));
		}

		String ckey = args[1].toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
		try (
				Connection connection = Yogbot.database.getConnection();
				PreparedStatement playerStmt = connection.prepareStatement(String.format(
						"SELECT discord_id FROM `%s` WHERE `ckey` = ? AND discord_id IS NOT NULL;", Yogbot.database.prefix("player")));
				PreparedStatement mentorCheckStmt = connection.prepareStatement(String.format(
						"SELECT ckey FROM `%s` WHERE `ckey` = ?;", Yogbot.database.prefix("mentor")));
				PreparedStatement mentorSetStatement = connection.prepareStatement(String.format(
						"INSERT INTO `%s` (`ckey`, `position`) VALUES (?, 'Mentor');", Yogbot.database.prefix("mentor")))
				){
			assert Yogbot.config.discordConfig != null;
			return giveRank(event, ckey, playerStmt, mentorCheckStmt, mentorSetStatement, Yogbot.config.discordConfig.mentorRole);
		} catch (SQLException e) {
			LOGGER.error("Error in AddAOCommand", e);
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
