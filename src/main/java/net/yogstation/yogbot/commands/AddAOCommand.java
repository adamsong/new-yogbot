package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class AddAOCommand extends PermissionsCommand {

	public AddAOCommand() {
		super("addao");
	}

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
						"SELECT * FROM `%s` WHERE `ckey` = ?;", Yogbot.database.prefix("admin")));
				PreparedStatement adminSetStmt = connection.prepareStatement(String.format(
						"INSERT INTO `%s` (`ckey`, `rank`) VALUES (?, 'Admin Observer');", Yogbot.database.prefix("admin")))
				){
			Mono<?> result = Mono.empty();
			playerStmt.setString(1, ckey);
			ResultSet playerResults = playerStmt.executeQuery();
			if(!playerResults.next()) {
				playerResults.close();
				return reply(event, "No user with this ckey has a linked discord account");
			}
			long discordID = playerResults.getLong("discord_id");
			if(playerResults.next()) {
				playerResults.close();
				return reply(event, "More than 1 of this ckey with a Discord ID, this makes no sense at all!");
			}
			playerResults.close();

			adminCheckStmt.setString(1, ckey);
			ResultSet adminCheckResults = adminCheckStmt.executeQuery();
			if(adminCheckResults.next()) {
				result = result.and(send(event, "User already has in-game rank."));
			} else {
				adminSetStmt.setString(1, ckey);
				adminSetStmt.execute();
				if(adminSetStmt.getUpdateCount() > 0) {
					result = result.and(send(event, "In game rank given successfully"));
				} else {
					result = result.and(send(event, "Failed to give in game rank"));
				}
			}
			adminCheckResults.close();

			return result.and(event.getGuild().flatMap(guild ->
				guild.getMemberById(Snowflake.of(discordID)).flatMap(member ->
				{
					assert Yogbot.config.discordConfig != null;
					return member.addRole(Snowflake.of(Yogbot.config.discordConfig.AORole)).and(send(event, "Added discord role"));
				})
			));
		} catch (SQLException e) {
			LOGGER.error("Error in AddAOCommand", e);
			return reply(event, "Unable to access database.");
		}
	}

	@Override
	public String getName() {
		return "addao";
	}
}
