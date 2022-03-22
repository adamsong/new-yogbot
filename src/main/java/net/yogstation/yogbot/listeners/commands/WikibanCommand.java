package net.yogstation.yogbot.listeners.commands;

import discord4j.common.util.Snowflake;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;

@Component
public class WikibanCommand extends ChannelBanCommand {
	public WikibanCommand(DiscordConfig discordConfig, PermissionsManager permissions) {
		super(discordConfig, permissions);
	}
	
	@Override
	protected Snowflake getBanRole() {
		return Snowflake.of(discordConfig.wikibanRole);
	}
	
	@Override
	protected String getRequiredPermissions() {
		return "wikiban";
	}
	
	@Override
	protected String getDescription() {
		return "Ban someone from the wiki channel.";
	}
	
	@Override
	public String getName() {
		return "wikiban";
	}
}
