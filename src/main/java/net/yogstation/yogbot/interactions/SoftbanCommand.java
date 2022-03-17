package net.yogstation.yogbot.interactions;

import discord4j.core.event.domain.interaction.UserInteractionEvent;
import reactor.core.publisher.Mono;

public class SoftbanCommand implements IInteractionHandler<UserInteractionEvent> {
	@Override
	public String getName() {
		return "Softban";
	}

	@Override
	public Mono<?> handle(UserInteractionEvent event) {
		return event.reply().withEphemeral(true).withContent("Banned");
	}
}
