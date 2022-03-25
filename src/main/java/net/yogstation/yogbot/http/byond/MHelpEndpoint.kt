package net.yogstation.yogbot.http.byond

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import net.yogstation.yogbot.config.DiscordChannelsConfig
import org.springframework.stereotype.Component

@Component
class MHelpEndpoint(private val channelsConfig: DiscordChannelsConfig, client: GatewayDiscordClient):
	MessageRelayEndpoint(client) {

	override val method = "mhelp"
	override val channelId: Snowflake = Snowflake.of(channelsConfig.channelMentor)

}
