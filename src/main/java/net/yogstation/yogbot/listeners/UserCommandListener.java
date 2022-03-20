package net.yogstation.yogbot.listeners;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import net.yogstation.yogbot.commands.ActivityCommand;
import net.yogstation.yogbot.commands.EightBallCommand;
import net.yogstation.yogbot.interactions.IInteractionHandler;
import net.yogstation.yogbot.interactions.KickCommand;
import net.yogstation.yogbot.interactions.SoftbanCommand;
import net.yogstation.yogbot.interactions.StaffBanCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class UserCommandListener {
	private static final List<IInteractionHandler<UserInteractionEvent>> commands = new ArrayList<>();
	
	static {
		commands.add(new SoftbanCommand());
		commands.add(new KickCommand());
		commands.add(new StaffBanCommand());
	}

	public static List<String> getCommandURIs() {
		return commands.stream().map(IInteractionHandler::getURI).toList();
	}
	
	public static Mono<?> handle(UserInteractionEvent event) {
		return Flux.fromIterable(commands)
			.filter(command -> command.getName().equals(event.getCommandName()))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
