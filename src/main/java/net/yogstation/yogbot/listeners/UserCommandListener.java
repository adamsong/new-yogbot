package net.yogstation.yogbot.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import net.yogstation.yogbot.listeners.interactions.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserCommandListener {
	private final List<IUserCommand> commands;

	public UserCommandListener(List<IUserCommand> commands, GatewayDiscordClient client) {
		this.commands = commands;
		
		client.on(UserInteractionEvent.class, this::handle).subscribe();
	}
	
	public Mono<?> handle(UserInteractionEvent event) {
		return Flux.fromIterable(commands)
			.filter(command -> command.getName().equals(event.getCommandName()))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
