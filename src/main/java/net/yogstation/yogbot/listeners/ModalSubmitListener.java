package net.yogstation.yogbot.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import net.yogstation.yogbot.listeners.interactions.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ModalSubmitListener {

	private final List<IModalSubmitHandler> commands;
	
	public ModalSubmitListener(List<IModalSubmitHandler> commands, GatewayDiscordClient client) {
		this.commands = commands;
		
		client.on(ModalSubmitInteractionEvent.class, this::handle).subscribe();
	}
	
	public Mono<?> handle(ModalSubmitInteractionEvent event) {
		return Flux.fromIterable(commands)
			.filter(command -> event.getCustomId().startsWith(command.getIdPrefix()))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
