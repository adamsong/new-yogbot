package net.yogstation.yogbot.http.byond

import com.fasterxml.jackson.databind.JsonNode
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import net.yogstation.yogbot.util.HttpUtil
import org.springframework.http.HttpEntity
import reactor.core.publisher.Mono

abstract class MessageRelayEndpoint protected constructor(val client: GatewayDiscordClient) : IByondEndpoint {
	abstract val channelId: Snowflake
	override fun receiveData(data: JsonNode): Mono<HttpEntity<String>> {
		val ckey = data.get("ckey")?.asText() ?: return HttpUtil.badRequest("Missing ckey parameter")
		val message = data.get("message")?.asText() ?: return HttpUtil.badRequest("Missing message parameter")
		return client.getChannelById(channelId)
			.flatMap { channel -> channel.restChannel.createMessage("**$ckey**: $message").then(HttpUtil.ok("Message sent")) }
	}
}
