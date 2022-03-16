package net.yogstation.yogbot;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import net.yogstation.yogbot.config.ConfigManager;
import net.yogstation.yogbot.listeners.SlashCommandListener;
import net.yogstation.yogbot.permissions.PermissionsManager;
import net.yogstation.yogbot.permissions.PermissionsNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class Yogbot {
	private static final Logger LOGGER = LoggerFactory.getLogger(Yogbot.class);

	public static final ConfigManager config = new ConfigManager();
	public static final PermissionsManager permissions = new PermissionsManager();

	public static void main(String[] args) {
		GatewayDiscordClient client = DiscordClientBuilder.create(config.discordConfig.botToken)
				.build().login().block();
		
		if (client == null) {
			System.err.println("Error making discord client");
			return;
		}
		
		//Call our code to handle creating/deleting/editing our global slash commands.
		try {
			GlobalCommandRegistrar registrar = new GlobalCommandRegistrar(client.getRestClient());
			registrar.addURIs(SlashCommandListener.getCommandURIs());
			registrar.registerCommands();
		} catch (Exception e) {
			LOGGER.error("Error trying to register global slash commands", e);
		}

		// Register the slash command listener
		client.on(ChatInputInteractionEvent.class, SlashCommandListener::handle).subscribe();

		// Finalize setup
		client.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
			final User self = event.getSelf();
			LOGGER.info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
		})).then(client.onDisconnect()).block();
	}
}
