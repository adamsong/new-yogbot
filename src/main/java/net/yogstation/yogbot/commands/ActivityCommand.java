package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class ActivityCommand extends PermissionsCommand implements ICommand<ChatInputInteractionEvent> {

	public ActivityCommand() {
		super("note");
	}

	@Override
	public String getName() {
		return "activity";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		if(!hasPermission(event)) return permissionError(event);
		return event.reply().withContent("This is where the activity will be");
	}
}
