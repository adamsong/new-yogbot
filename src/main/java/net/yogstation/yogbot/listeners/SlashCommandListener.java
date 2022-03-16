package net.yogstation.yogbot.listeners;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import net.yogstation.yogbot.commands.ActivityCommand;
import net.yogstation.yogbot.commands.ICommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandListener {
	private static final List<ICommand<ChatInputInteractionEvent>> commands = new ArrayList<>();
	
	static {
		commands.add(new ActivityCommand());
	}

	public static List<String> getCommandURIs() {
		return commands.stream().map(ICommand::getURI).toList();
	}
	
	public static Mono<Void> handle(ChatInputInteractionEvent event) {
		return Flux.fromIterable(commands)
			.filter(command -> command.getName().equals(event.getCommandName()))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
