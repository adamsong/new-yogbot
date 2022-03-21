package net.yogstation.yogbot;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import discord4j.rest.RestClient;
import net.yogstation.yogbot.config.DiscordConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Mono;

import java.util.Random;

@SpringBootApplication
@EnableScheduling
public class Yogbot {
	private static final Logger LOGGER = LoggerFactory.getLogger(Yogbot.class);
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(Yogbot.class).build().run(args);
	}
	
	@Bean
	public GatewayDiscordClient getGatewayDiscordClient(DiscordConfig config) {
		GatewayDiscordClient client = DiscordClientBuilder.create(config.botToken).build().login().block();
		if (client == null) return null;
		client.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
			final User self = event.getSelf();
			LOGGER.info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
		})).subscribe();
		return client;
	}
	
	@Bean
	public RestClient getRestClient(GatewayDiscordClient client) {
		return client.getRestClient();
	}
	
	@Bean
	public Random getRandom() {
		return new Random();
	}
}
