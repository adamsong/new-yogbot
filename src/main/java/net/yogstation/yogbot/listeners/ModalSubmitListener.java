package net.yogstation.yogbot.listeners;

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import net.yogstation.yogbot.interactions.IInteractionHandler;
import net.yogstation.yogbot.interactions.IModalSubmitHandler;
import net.yogstation.yogbot.interactions.KickCommand;
import net.yogstation.yogbot.interactions.SoftbanCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class ModalSubmitListener  {

	private static final List<IModalSubmitHandler> commands = new ArrayList<>();

	static {
		commands.add(new SoftbanCommand());
		commands.add(new KickCommand());
	}

	public static Mono<?> handle(ModalSubmitInteractionEvent event) {
		return Flux.fromIterable(commands)
			.filter(command -> event.getCustomId().startsWith(command.getIdPrefix()))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
