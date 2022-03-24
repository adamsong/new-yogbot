package net.yogstation.yogbot.http.byond

import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.core.GatewayDiscordClient
import net.yogstation.yogbot.DatabaseManager
import net.yogstation.yogbot.config.DiscordConfig
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ASayMessageEndpoint(webClient: WebClient, mapper: ObjectMapper, database: DatabaseManager,
						  client: GatewayDiscordClient, discordConfig: DiscordConfig) : MessageEndpoint(webClient, mapper, database, client, discordConfig) {

	override val method: String
		get() = "asaymessage"

	override val webhookUrl: String
		get() = discordConfig.asayWebhookUrl
}
