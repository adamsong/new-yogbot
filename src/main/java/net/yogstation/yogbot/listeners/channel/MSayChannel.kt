package net.yogstation.yogbot.listeners.channel

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent
import net.yogstation.yogbot.ByondConnector
import net.yogstation.yogbot.config.DiscordChannelsConfig
import org.apache.commons.text.StringEscapeUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class MSayChannel(channelsConfig: DiscordChannelsConfig, private val byondConnector: ByondConnector) : AbstractChannel(
	channelsConfig
) {
	override val channel: Snowflake
		get() = Snowflake.of(channelsConfig.channelMsay)

	override fun handle(event: MessageCreateEvent): Mono<*> {
		if (event.message.author.isEmpty) return Mono.empty<Any>()
		val message = StringEscapeUtils.escapeHtml4(event.message.content)
		val mentorName: String = if (event.member.isPresent) {
			event.member.get().displayName
		} else {
			event.message.author.get().username
		}
		byondConnector.request(
			"?msay=${URLEncoder.encode(message, StandardCharsets.UTF_8)}&admin=${
				URLEncoder.encode(
					mentorName,
					StandardCharsets.UTF_8
				)
			}"
		)
		return Mono.empty<Any>()
	}
}
