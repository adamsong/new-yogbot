package net.yogstation.yogbot.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.listeners.commands.*;
import net.yogstation.yogbot.config.DiscordConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextCommandListener {
	private final List<TextCommand> commands;
	private final DiscordConfig config;
	
	public TextCommandListener(List<TextCommand> commands, GatewayDiscordClient client, DiscordConfig config) {
		this.commands = commands;
		this.config = config;
		
		client.on(MessageCreateEvent.class, this::handle).subscribe();
	}
	
	public List<String> getHelpMessages(Member member, boolean hidden) {
		List<String> messages = new ArrayList<>();
		for(TextCommand command : commands) {
			if(command.isHidden() != hidden) continue;
			if(command instanceof PermissionsCommand && !((PermissionsCommand) command).hasPermission(member))
				continue;
			messages.add(command.getHelpText());
		}
		return messages;
	}
	
	public Mono<?> handle(MessageCreateEvent event) {
		return Flux.fromIterable(commands)
			.filter(command -> event.getMessage().getContent().startsWith(
					config.commandPrefix +
					command.getName()
			))
			.next()
			.flatMap(command -> command.handle(event));
	}
}
