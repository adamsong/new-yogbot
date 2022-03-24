package net.yogstation.yogbot.util

import net.yogstation.yogbot.config.DiscordChannelsConfig
import java.util.HashSet
import java.util.Arrays

object ChannelUtil {
	fun isAdminChannel(channelID: Long, config: DiscordChannelsConfig): Boolean {
		val adminChannelIds: Set<Long> = HashSet(
			listOf(
				config.channelAdmemes, config.channelAdmin, config.channelCouncil,
				config.channelAdminBotspam
			)
		)
		return adminChannelIds.contains(channelID)
	}
}
