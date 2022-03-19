package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import net.yogstation.yogbot.Yogbot;

public class LoreBanCommand extends ChannelBanCommand {

	@Override
	protected Snowflake getBanRole() {
		return Snowflake.of(Yogbot.config.discordConfig.loreBanRole);
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
