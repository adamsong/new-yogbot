package net.yogstation.yogbot.http.byond;

import com.fasterxml.jackson.databind.JsonNode;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.rest.util.Color;
import net.yogstation.yogbot.config.ByondConfig;
import net.yogstation.yogbot.config.DiscordChannelsConfig;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.util.HttpUtil;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ServerStatusEndpoint implements IByondEndpoint{
	private final ByondConfig byondConfig;
	private final DiscordChannelsConfig channelsConfig;
	private final GatewayDiscordClient client;
	private final DiscordConfig discordConfig;
	
	public ServerStatusEndpoint(ByondConfig byondConfig, DiscordChannelsConfig channelsConfig,
	                            GatewayDiscordClient client, DiscordConfig discordConfig) {
		this.byondConfig = byondConfig;
		this.channelsConfig = channelsConfig;
		this.client = client;
		this.discordConfig = discordConfig;
	}
	
	@Override
	public String getMethod() {
		return "roundstatus";
	}
	
	@Override
	public Mono<HttpEntity<String>> receiveData(JsonNode data) {
		JsonNode status = data.get("status");
		if(status == null) return HttpUtil.badRequest("Missing status");
		
		if(status.asText().equals("lobby")) {
			EmbedCreateSpec.Builder embed = EmbedCreateSpec.builder();
			embed.author("New round notifier", "", "https://i.imgur.com/GPZgtbe.png");
			embed.description(String.format("A new round is about to begin! Join now at %s", byondConfig.serverJoinAddress));
			embed.addField("Map Name", data.get("map_name").asText(), true);
			embed.addField("Revision", data.get("revision").asText(), true);
			embed.addField("Round Number", data.get("round").asText(), true);
			embed.addField("Changelog", "No Changes", true);
			embed.color(Color.of(0x62F442));
			return client.getChannelById(Snowflake.of(channelsConfig.channelBotspam)).flatMap(channel ->
				channel.getRestChannel().createMessage(MessageCreateRequest.builder().content(String.format("<@&%s>", discordConfig.subscriberRole)).embed(embed.build().asRequest()).build())
			).and(client.updatePresence(ClientPresence.online(ClientActivity.playing("Round Starting!")))).then(HttpUtil.ok("Status set"));
		}
		if(status.asText().equals("ingame")) {
			return client.updatePresence(ClientPresence.online(ClientActivity.playing("In Game"))).then(HttpUtil.ok("Status set"));
		}
		return client.updatePresence(ClientPresence.online(ClientActivity.playing("Round Ending"))).then(HttpUtil.ok("Status set"));
	}
}
