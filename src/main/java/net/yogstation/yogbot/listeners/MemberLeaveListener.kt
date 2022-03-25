package net.yogstation.yogbot.listeners

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.guild.MemberLeaveEvent
import net.yogstation.yogbot.config.DiscordChannelsConfig
import net.yogstation.yogbot.util.DiscordUtil
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MemberLeaveListener(val client: GatewayDiscordClient, val channelsConfig: DiscordChannelsConfig) {

	init {
		client.on(MemberLeaveEvent::class.java) { this.handle(it) }.subscribe()
	}

	fun handle(event: MemberLeaveEvent): Mono<*> {
		return DiscordUtil.logToChannel("**${event.user.username}#${event.user.discriminator}** left the server", client, Snowflake.of(channelsConfig.channelPublicLog));
	}
}
