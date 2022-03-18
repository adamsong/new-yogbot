package net.yogstation.yogbot.listeners;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.commands.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class MessageCreateListener {
	private static final List<IEventHandler<MessageCreateEvent>> commands = new ArrayList<>();
	
	static {
		commands.add(new ActivityCommand());
		commands.add(new AddAOCommand());
		commands.add(new AddMentorCommand());
		commands.add(new AshCommand());
		commands.add(new BannuCommand());
		commands.add(new BugCommand());
		commands.add(new CoderCommand());
		commands.add(new CorgiCommand());
		commands.add(new CouncilCommand());
		commands.add(new DuckCommand());
		commands.add(new EggrpCommand());
		commands.add(new EightBallCommand());
		commands.add(new FoxesCommand());
		commands.add(new HardyCommand());
	}
	
	public static Mono<?> handle(MessageCreateEvent event) {
		return Flux.fromIterable(commands)
			.filter(command -> event.getMessage().getContent().startsWith(
					Yogbot.config.discordConfig.commandPrefix +
					command.getName()
			))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
