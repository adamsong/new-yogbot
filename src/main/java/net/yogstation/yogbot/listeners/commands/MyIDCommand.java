package net.yogstation.yogbot.listeners.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MyIDCommand extends TextCommand {
	public MyIDCommand(DiscordConfig discordConfig) {
		super(discordConfig);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		if(event.getMessage().getAuthor().isEmpty()) return Mono.empty();
		return reply(event, "Your ID is %s", event.getMessage().getAuthor().get().getId().asString());
	}

	@Override
	protected String getDescription() {
		return "Provides your Discord ID, no longer used for account linking";
	}

	@Override
	public String getName() {
		return "myid";
	}
}
