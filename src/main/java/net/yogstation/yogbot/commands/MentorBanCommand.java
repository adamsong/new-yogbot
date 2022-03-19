package net.yogstation.yogbot.commands;

import discord4j.common.util.Snowflake;
import net.yogstation.yogbot.Yogbot;

public class MentorBanCommand extends ChannelBanCommand {
	@Override
	protected Snowflake getBanRole() {
		return Snowflake.of(Yogbot.config.discordConfig.mentorBanRole);
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
