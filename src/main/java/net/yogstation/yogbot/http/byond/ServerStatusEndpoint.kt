package net.yogstation.yogbot.http.byond

import com.fasterxml.jackson.databind.JsonNode
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.Channel
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.MessageCreateRequest
import discord4j.rest.util.Color
import net.yogstation.yogbot.config.ByondConfig
import net.yogstation.yogbot.config.DiscordChannelsConfig
import net.yogstation.yogbot.config.DiscordConfig
import net.yogstation.yogbot.util.HttpUtil
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ServerStatusEndpoint(private val byondConfig: ByondConfig, private val channelsConfig: DiscordChannelsConfig,
						   private val client: GatewayDiscordClient, private val discordConfig: DiscordConfig) : IByondEndpoint {
	override val method: String
		get() = "roundstatus"

	override fun receiveData(data: JsonNode): Mono<HttpEntity<String>> {
		val status = data["status"]
			?: return HttpUtil.badRequest("Missing status")
		if (status.asText() == "lobby") {
			val embed = EmbedCreateSpec.builder()
			embed.author("New round notifier", "", "https://i.imgur.com/GPZgtbe.png")
			embed.description(String.format("A new round is about to begin! Join now at %s", byondConfig.serverJoinAddress))
			embed.addField("Map Name", data["map_name"].asText(), true)
			embed.addField("Revision", data["revision"].asText(), true)
			embed.addField("Round Number", data["round"].asText(), true)
			embed.addField("Changelog", "No Changes", true)
			embed.color(Color.of(0x62F442))
			return client.getChannelById(Snowflake.of(channelsConfig.channelBotspam)).flatMap { channel: Channel -> channel.restChannel.createMessage(MessageCreateRequest.builder().content(String.format("<@&%s>", discordConfig.subscriberRole)).embed(embed.build().asRequest()).build()) }
				.and(client.updatePresence(ClientPresence.online(ClientActivity.playing("Round Starting!")))).then(HttpUtil.ok("Status set"))
		}
		return if (status.asText() == "ingame") {
			client.updatePresence(ClientPresence.online(ClientActivity.playing("In Game"))).then(HttpUtil.ok("Status set"))
		} else client.updatePresence(ClientPresence.online(ClientActivity.playing("Round Ending"))).then(HttpUtil.ok("Status set"))
	}
}
