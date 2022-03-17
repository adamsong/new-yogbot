package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AddRankCommand extends PermissionsCommand {
	public AddRankCommand(String requiredPermission) {
		super(requiredPermission);
	}

	protected Mono<?> giveRank(MessageCreateEvent event, String ckey, PreparedStatement playerStmt, PreparedStatement rankCheckStmt, PreparedStatement rankSetStmt, long role) throws SQLException {
		Mono<?> result = Mono.empty();
		playerStmt.setString(1, ckey);
		ResultSet playerResults = playerStmt.executeQuery();
		if (!playerResults.next()) {
			playerResults.close();
			return reply(event, "No user with this ckey has a linked discord account");
		}
		long discordID = playerResults.getLong("discord_id");
		if (playerResults.next()) {
			playerResults.close();
			return reply(event, "More than 1 of this ckey with a Discord ID, this makes no sense at all!");
		}
		playerResults.close();

		rankCheckStmt.setString(1, ckey);
		ResultSet adminCheckResults = rankCheckStmt.executeQuery();
		if (adminCheckResults.next()) {
			result = result.and(send(event, "User already has in-game rank."));
		} else {
			rankSetStmt.setString(1, ckey);
			rankSetStmt.execute();
			if (rankSetStmt.getUpdateCount() > 0) {
				result = result.and(send(event, "In game rank given successfully"));
			} else {
				result = result.and(send(event, "Failed to give in game rank"));
			}
		}
		adminCheckResults.close();

		return result.and(event.getGuild().flatMap(guild ->
				guild.getMemberById(Snowflake.of(discordID)).flatMap(member ->
						member.addRole(Snowflake.of(role)).and(send(event, "Added discord role")))
		));
	}
}
