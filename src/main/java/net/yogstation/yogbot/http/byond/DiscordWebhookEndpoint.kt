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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

abstract class DiscordWebhookEndpoint(
	private val webClient: WebClient, protected val mapper: ObjectMapper, protected val database: DatabaseManager,
	protected val client: GatewayDiscordClient, protected val discordConfig: DiscordConfig) : IByondEndpoint {
	protected val logger: Logger = LoggerFactory.getLogger(javaClass)

	protected abstract val webhookUrl: String

	override fun receiveData(data: JsonNode): Mono<HttpEntity<String>> {
		val ckey = data["ckey"]
		val message = data["message"]
		if (ckey == null) return HttpUtil.badRequest("Missing ckey")
		if (message == null) return HttpUtil.badRequest("Missing message")
		val node = mapper.createObjectNode()
		node.set<JsonNode>("username", ckey)
		node.set<JsonNode>("content", message)
		node.set<JsonNode>("allowed_mentions", mapper.createObjectNode().set("parse", mapper.createArrayNode()))
		val result = ByondLinkUtil.getMemberID(StringUtils.ckeyIze(ckey.asText().split("/").toTypedArray()[0]), database)
		return if (result.hasValue()) {
			client.getMemberById(Snowflake.of(discordConfig.mainGuildID), result.value!!).flatMap { member: Member ->
				val animated = member.hasAnimatedGuildAvatar()
				val avatar = member.getGuildAvatarUrl(if (animated) Image.Format.GIF else Image.Format.PNG).orElse(member.avatarUrl)
				node.put("avatar_url", avatar)
				sendData(node)
			}
		} else sendData(node)
	}

	protected fun sendData(webhookData: ObjectNode?): Mono<HttpEntity<String>> {
		return try {
			webClient.post()
				.uri(URI.create(webhookUrl))
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(mapper.writer().writeValueAsString(webhookData)))
				.retrieve()
				.toBodilessEntity().flatMap { HttpUtil.ok("Sent webhook message") }
		} catch (e: JsonProcessingException) {
			logger.error("Failed to make webhook json")
			HttpUtil.response("Error making webhook json", HttpStatus.INTERNAL_SERVER_ERROR)
		}
	}
}
