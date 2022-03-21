package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import org.springframework.stereotype.Component;

@Component
public class LoreBanCommand extends ChannelBanCommand {
	
	public LoreBanCommand(DiscordConfig discordConfig, PermissionsManager permissions) {
		super(discordConfig, permissions);
	}
	
	@Override
	protected Snowflake getBanRole() {
		return Snowflake.of(discordConfig.loreBanRole);
	}

	@Override
	protected String getRequiredPermissions() {
		return "loreban";
	}

	@Override
	protected String getDescription() {
		return "Ban someone from the lore channel";
	}

	@Override
	public String getName() {
		return "loreban";
	}
}
