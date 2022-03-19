package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class MyIDCommand extends TextCommand {
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		if(event.getMessage().getAuthor().isEmpty()) return Mono.empty();
		return reply(event, String.format("Your ID is %s", event.getMessage().getAuthor().get().getId().asString()));
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
