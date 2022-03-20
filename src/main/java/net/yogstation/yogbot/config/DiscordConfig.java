package net.yogstation.yogbot.config;

public class DiscordConfig {
	public String botToken = "No token here";

	public String commandPrefix = "!";
	public String serverName = "Yogstation 13";

	public boolean useLocalCommands = false;

	public long mainGuildID = 134720091576205312L;
	public long aoRole = 471634210923216906L;
	public long mentorRole = 505280515322937355L;
	public long jesterRole = 539846767827746817L;

	// Punishment Roles
	public long softBanRole = 302093934551629827L;
	public long loreBanRole = 716784185192480798L;
	public long mentorBanRole = 865043884170149928L;
	
	public long firstWarningRole = 514066643119505408L;
	public long secondWarningRole = 514066798216347662L;
	public long staffPublicBanRole = 290926097028218884L;
}
