package net.yogstation.yogbot.config;

import discord4j.common.util.Snowflake;

public class DiscordConfig {
	/**
	 * The token for the discord bot
	 */
	public String botToken = "No token here";

	public String commandPrefix = "!";

	public boolean useLocalCommands = false;

	public long mainGuildID = 134720091576205312L;
	public long AORole = 471634210923216906L;
}
