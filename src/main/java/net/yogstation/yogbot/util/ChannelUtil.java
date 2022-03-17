package net.yogstation.yogbot.util;

import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.config.DiscordChannelsConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChannelUtil {
	public static boolean isAdminChannel(long channelID) {
		DiscordChannelsConfig config = Yogbot.config.channelsConfig;
		final Set<Long> adminChannelIds = new HashSet<>(Arrays.asList(
			config.channelAdmemes,
				config.channelAdmin,
				config.channelCouncil,
				config.channelAdminBotspam
		));
		return adminChannelIds.contains(channelID);
	}
}
