package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;

public class ActivityCommand extends PermissionsCommand {

	public ActivityCommand() {
		super("note");
	}

	@Override
	public String getName() {
		return "activity";
	}

	@Override
	public Mono<?> doCommand(MessageCreateEvent event) {
		return reply(event, "Here is the activity");
	}
}
