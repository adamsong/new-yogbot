package net.yogstation.yogbot.config;

import discord4j.common.util.Snowflake;
import org.springframework.stereotype.Component;

@Component
public class DiscordConfig extends ConfigClass {
	
	public DiscordConfig() {
		loadConfig("discord.properties");
	}
	
	
	public String botToken = "No token here";
	
	public String commandPrefix = "!";
	public String serverName = "Yogstation 13";
	
	public boolean useLocalCommands = false;
	
	public String oauthAuthorizeUrl = "https://bab.yogstation.net/auth/authorize";
	public String oauthTokenUrl = "https://bab.yogstation.net/auth/token";
	public String oauthUserInfoUrl = "https://bab.yogstation.net/auth/userinfo";
	public String oauthClientId = "yogstation_yogbot";
	public String oauthClientSecret = "lolno";
	
	public String asayWebhookUrl = "";
	public String msayWebhookUrl = "";
	
	public long mainGuildID = 134720091576205312L;
	public long aoRole = 471634210923216906L;
	public long mentorRole = 505280515322937355L;
	public long jesterRole = 539846767827746817L;
	public long subscriberRole = 213375106888499200L;
	
	public long manualVerifyRole = 768196461313523727L;
	public long byondVerificationRole = 762005208326733845L;
	
	// Punishment Roles
	public long softBanRole = 302093934551629827L;
	public long loreBanRole = 716784185192480798L;
	public long mentorBanRole = 865043884170149928L;
	public long wikibanRole = 792982865989992490L;
	
	public long firstWarningRole = 514066643119505408L;
	public long secondWarningRole = 514066798216347662L;
	public long staffPublicBanRole = 290926097028218884L;
}
