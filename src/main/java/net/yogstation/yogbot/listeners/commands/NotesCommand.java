package net.yogstation.yogbot.listeners.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import net.yogstation.yogbot.util.MonoCollector;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class NotesCommand extends GetNotesCommand {
	
	public NotesCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions, database);
	}
	
	@Override
	protected String getRequiredPermissions() {
		return "note";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		List<String> notes = List.of("An unknown error has occurred");
		if(target == null)
			notes = List.of(String.format("Usage is `%snotes <ckey or @Username>`", discordConfig.commandPrefix));
		else {
			if(target.getCkey() == null) {
				String populateResult = target.populate(database);
				if(populateResult != null) notes = List.of(populateResult);
			}
			if(target.getCkey() != null) {
				notes = getNotes(target.getCkey(), true);
			}
		}
		List<String> finalNotes = notes;
		return event.getMessage().getChannel().flatMap(messageChannel ->
			finalNotes.stream().map(messageChannel::createMessage).collect(MonoCollector.toMono()));
	}

	@Override
	protected String getDescription() {
		return "Check a user's notes";
	}

	@Override
	public String getName() {
		return "notes";
	}
}
