package net.yogstation.yogbot.http.byond

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.Member
import discord4j.rest.util.Image
import net.yogstation.yogbot.DatabaseManager
import net.yogstation.yogbot.config.DiscordConfig
import net.yogstation.yogbot.util.ByondLinkUtil
import net.yogstation.yogbot.util.HttpUtil
import net.yogstation.yogbot.util.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

class EndpointTicket(webClient: WebClient, mapper: ObjectMapper, database: DatabaseManager,
							  client: GatewayDiscordClient, discordConfig: DiscordConfig) : DiscordWebhookEndpoint(webClient, mapper, database, client, discordConfig) {

	override val webhookUrl = discordConfig.ticketWebhookUrl
	override val method = "ticket"

	override fun receiveData(data: JsonNode): Mono<HttpEntity<String>> {
		val user = data["user"]
		val ticketid = data["ticketid"]
		val roundid = data["roundid"]
		val message = data["message"]
		if (user == null) return HttpUtil.badRequest("Missing ckey")
		if (ticketid == null) return HttpUtil.badRequest("Missing ticketid")
		if (roundid == null) return HttpUtil.badRequest("Roundid missing")
		if (message == null) return HttpUtil.badRequest("Missing message")
		val node = mapper.createObjectNode()
		node.set<JsonNode>("username", roundid)
		node.put("content", "**${user.asText()}, Ticket #${ticketid.asText()}:** ${message.asText()}")
		node.set<JsonNode>("allowed_mentions", mapper.createObjectNode().set("parse", mapper.createArrayNode()))
		return sendData(node)
	}
}
