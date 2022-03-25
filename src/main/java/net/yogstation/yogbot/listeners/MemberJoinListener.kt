package net.yogstation.yogbot.listeners

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.guild.MemberJoinEvent
import net.yogstation.yogbot.config.DiscordChannelsConfig
import net.yogstation.yogbot.util.DiscordUtil
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MemberJoinListener(val client: GatewayDiscordClient, val channelsConfig: DiscordChannelsConfig) {

	init {
		client.on(MemberJoinEvent::class.java) { this.handle(it) }.subscribe()
	}

	fun handle(event: MemberJoinEvent): Mono<*> {
		return DiscordUtil.logToChannel("**${event.member.username}#${event.member.discriminator}** joined the server", client, Snowflake.of(channelsConfig.channelPublicLog));
	}
}
