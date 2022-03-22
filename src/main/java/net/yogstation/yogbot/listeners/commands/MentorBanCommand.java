package net.yogstation.yogbot.listeners.commands;

import discord4j.common.util.Snowflake;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;

@Component
public class MentorBanCommand extends ChannelBanCommand {
	
	public MentorBanCommand(DiscordConfig discordConfig, PermissionsManager permissions) {
		super(discordConfig, permissions);
	}
	
	@Override
	protected Snowflake getBanRole() {
		return Snowflake.of(discordConfig.mentorBanRole);
	}

	@Override
	protected String getRequiredPermissions() {
		return "mentorban";
	}

	@Override
	protected String getDescription() {
		return "Ban someone from the mentor channel";
	}

	@Override
	public String getName() {
		return "mentorban";
	}
}
