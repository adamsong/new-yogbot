package net.yogstation.yogbot.http.byond;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.GatewayDiscordClient;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MSayMessageEndpoint extends MessageEndpoint {
	
	public MSayMessageEndpoint(WebClient webClient, ObjectMapper mapper, DatabaseManager database, GatewayDiscordClient client, DiscordConfig discordConfig) {
		super(webClient, mapper, database, client, discordConfig);
	}
	
	@Override
	public String getMethod() {
		return "msaymessage";
	}
	
	@Override
	protected String getWebhookUrl() {
		return discordConfig.msayWebhookUrl;
	}
}
