package net.yogstation.yogbot.util;

import discord4j.common.util.Snowflake;
import net.yogstation.yogbot.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByondLinkUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ByondLinkUtil.class);

	public static Result<Snowflake, String> getMemberID(String ckey, DatabaseManager database) {
		try(
			Connection connection = database.getConnection();
			PreparedStatement playerStmt = connection.prepareStatement(String.format(
				"SELECT discord_id FROM `%s` WHERE `ckey` = ? AND discord_id IS NOT NULL;", database.prefix("player")))
			) {
			playerStmt.setString(1, ckey);
			ResultSet playerResults = playerStmt.executeQuery();
			if (!playerResults.next()) {
				playerResults.close();
				return Result.error("No user with this ckey has a linked discord account");
			}
			long discordID = playerResults.getLong("discord_id");
			if (playerResults.next()) {
				playerResults.close();
				return Result.error("More than 1 of this ckey with a Discord ID, this makes no sense at all!");
			}
			playerResults.close();
			return Result.success(Snowflake.of(discordID));
		} catch (SQLException e) {
			LOGGER.error("SQL Error", e);
			return Result.error("A SQL Error has occurred.");
		}
	}

	public static Result<String, String> getCkey(Snowflake snowflake, DatabaseManager database) {
		try (Connection connection = database.getConnection();
			 PreparedStatement ckeyStmt = connection.prepareStatement(String.format(
				 "SELECT ckey FROM `%s` WHERE `discord_id` = ?", database.prefix("player")))
		) {
			ckeyStmt.setLong(1, snowflake.asLong());
			ResultSet ckeyResults = ckeyStmt.executeQuery();
			if (!ckeyResults.next()) {
				ckeyResults.close();
				return Result.error("Cannot find linked byond account.");
			}
			String ckey = ckeyResults.getString("ckey");
			if (ckeyResults.next()) {
				ckeyResults.close();
				return Result.error("Multiple accounts linked to discord ID");
			}
			ckeyResults.close();
			return Result.success(ckey);
		} catch (SQLException e) {
			LOGGER.error("Error getting notes", e);
			return Result.error("A SQL Error has occurred");
		}
	}
}
