package net.yogstation.yogbot.listeners

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.guild.BanEvent
import net.yogstation.yogbot.config.DiscordChannelsConfig
import net.yogstation.yogbot.util.DiscordUtil
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class BanListener(val client: GatewayDiscordClient, val channelsConfig: DiscordChannelsConfig) {

	init {
		client.on(BanEvent::class.java) { this.handle(it) }.subscribe()
	}

	fun handle(event: BanEvent): Mono<*> {
		return DiscordUtil.logToChannel("**${event.user.username}#${event.user.discriminator}** was banned from the server", client, Snowflake.of(channelsConfig.channelPublicLog))
	}
}
