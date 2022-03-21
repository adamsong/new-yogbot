package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.listeners.TextCommandListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelpCommand extends TextCommand {
	
	private final List<String> helpStrings;
	
	public HelpCommand(DiscordConfig discordConfig, List<TextCommand> commands) {
		super(discordConfig);
		helpStrings = commands.stream().map(TextCommand::getHelpText).toList();
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Member member = event.getMember().orElse(null);
		boolean hidden = event.getMessage().getContent().contains("hidden");

		StringBuilder output = new StringBuilder("Available Commands:\n");
		for(String helpString : helpStrings) {
			output.append(helpString);
			output.append("\n");
		}
		return reply(event, output.toString());
	}

	@Override
	protected String getDescription() {
		return "Displays a list of commands you have access to\n" +
			String.format("    `%s%s hidden` - Displays hidden commands", discordConfig.commandPrefix, getName());
	}

	@Override
	public String getName() {
		return "help";
	}
}
