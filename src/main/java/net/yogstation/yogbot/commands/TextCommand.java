package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.MessageCreateSpec;
import net.yogstation.yogbot.listeners.IEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public abstract class TextCommand implements IEventHandler<MessageCreateEvent> {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	@Override
	public Mono<?> handle(MessageCreateEvent event) {
		if(!canFire(event)) return doError(event);
		return doCommand(event);
	}

	protected abstract Mono<?> doCommand(MessageCreateEvent event);

	protected Mono<?> doError(MessageCreateEvent event) {
		return Mono.empty();
	}

	protected boolean canFire(MessageCreateEvent event) {
		return true;
	}

	protected Mono<?> reply(MessageCreateEvent event, String message) {
		return event.getMessage().getChannel().flatMap(channel ->
			channel.createMessage(MessageCreateSpec.builder()
					.messageReference(event.getMessage().getId())
					.content(message).build())
		);
	}

	protected Mono<?> send(MessageCreateEvent event, String message) {
		return event.getMessage().getChannel().flatMap(channel ->
				channel.createMessage(message));
	}
}
