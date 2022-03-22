package net.yogstation.yogbot.http.byond;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.util.ByondLinkUtil;
import net.yogstation.yogbot.util.HttpUtil;
import net.yogstation.yogbot.util.Result;
import net.yogstation.yogbot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static discord4j.rest.util.Image.Format.GIF;
import static discord4j.rest.util.Image.Format.PNG;

@Component
public class ASayMessageEndpoint implements IByondEndpoint {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private final WebClient webClient;
	private final ObjectMapper mapper;
	private final DatabaseManager database;
	private final GatewayDiscordClient client;
	private final DiscordConfig discordConfig;
	
	public ASayMessageEndpoint(WebClient webClient, ObjectMapper mapper, DatabaseManager database, GatewayDiscordClient client, DiscordConfig discordConfig) {
		this.webClient = webClient;
		this.mapper = mapper;
		this.database = database;
		this.client = client;
		this.discordConfig = discordConfig;
	}
	
	@Override
	public String getMethod() {
		return "asaymessage";
	}
	
	@Override
	public Mono<HttpEntity<String>> receiveData(JsonNode data) {
		JsonNode ckey = data.get("ckey");
		JsonNode message = data.get("message");
		
		if(ckey == null) return HttpUtil.badRequest("Missing ckey");
		if(message == null) return HttpUtil.badRequest("Missing message");
		
		ObjectNode node = mapper.createObjectNode();
		node.set("username", ckey);
		node.set("content", message);
		node.set("allowed_mentions", mapper.createObjectNode().set("parse", mapper.createArrayNode()));
		
		Result<Snowflake, String> result = ByondLinkUtil.getMemberID(StringUtils.ckey_ize(ckey.asText().split("/")[0]), database);
		if(result.hasValue()) {
			return client.getMemberById(Snowflake.of(discordConfig.mainGuildID), result.getValue()).flatMap(member -> {
				final boolean animated = member.hasAnimatedGuildAvatar();
				String avatar = member.getGuildAvatarUrl(animated ? GIF : PNG).orElse(member.getAvatarUrl());
				node.put("avatar_url", avatar);
				return sendData(node);
			});
		}
		return sendData(node);
	}
	
	private Mono<HttpEntity<String>> sendData(ObjectNode webhookData) {
		try {
			return webClient.post()
				.uri(URI.create(discordConfig.asayWebhookUrl))
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(mapper.writer().writeValueAsString(webhookData)))
				.retrieve()
				.toBodilessEntity().flatMap(ignored -> HttpUtil.ok("Sent webhook message"));
		} catch (JsonProcessingException e) {
			LOGGER.error("Failed to make webhook json");
			return HttpUtil.response("Error making webhook json", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
