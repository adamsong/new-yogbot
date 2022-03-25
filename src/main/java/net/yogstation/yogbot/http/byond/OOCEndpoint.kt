package net.yogstation.yogbot.http.byond

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import net.yogstation.yogbot.config.DiscordChannelsConfig

class OOCEndpoint(client: GatewayDiscordClient, private val channelsConfig: DiscordChannelsConfig) : MessageRelayEndpoint(client) {
	override val method = "oocmessage"
	override val channelId: Snowflake = Snowflake.of(channelsConfig.channelOOC)
}
