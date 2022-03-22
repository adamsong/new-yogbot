package net.yogstation.yogbot.listeners.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import reactor.core.publisher.Mono;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class EditRankCommand extends PermissionsCommand {
	
	protected final DatabaseManager database;
	
	public EditRankCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions);
		this.database = database;
	}
	
	protected Mono<?> giveRank(MessageCreateEvent event, CommandTarget target, PreparedStatement rankCheckStmt, PreparedStatement rankSetStmt, long role) throws SQLException {
		String errors = target.populate(database);
		if(errors != null) return reply(event, errors);

		Mono<?> result = Mono.empty();
		rankCheckStmt.setString(1, target.getCkey());
		ResultSet rankCheckResults = rankCheckStmt.executeQuery();
		if (rankCheckResults.next()) {
			result = result.and(send(event, "User already has in-game rank."));
		} else {
			rankSetStmt.setString(1, target.getCkey());
			rankSetStmt.execute();
			if (rankSetStmt.getUpdateCount() > 0) {
				result = result.and(send(event, "In game rank given successfully"));
			} else {
				result = result.and(send(event, "Failed to give in game rank"));
			}
		}
		rankCheckResults.close();

		return result.and(event.getGuild().flatMap(guild ->
				guild.getMemberById(target.getSnowflake()).flatMap(member ->
						member.addRole(Snowflake.of(role)).and(send(event, "Added discord role")))
		));
	}

	protected Mono<?> removeRank(MessageCreateEvent event, CommandTarget target, PreparedStatement rankSetStmt, long role) throws SQLException {
		String errors = target.populate(database);
		if(errors != null) return reply(event, errors);

		Mono<?> result = Mono.empty();
		rankSetStmt.setString(1, target.getCkey());
		rankSetStmt.execute();
		if (rankSetStmt.getUpdateCount() > 0) {
			result = result.and(send(event, "In game rank removed successfully"));
		} else {
			result = result.and(send(event, "Failed to remove in game rank"));
		}

		return result.and(event.getGuild().flatMap(guild ->
			guild.getMemberById(target.getSnowflake()).flatMap(member ->
				member.removeRole(Snowflake.of(role)).and(send(event, "Removed discord role")))
		));
	}
}
