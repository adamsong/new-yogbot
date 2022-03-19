package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.PartialMember;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.ByondLinkUtil;
import net.yogstation.yogbot.util.MonoCollector;
import net.yogstation.yogbot.util.Result;
import reactor.core.publisher.Mono;

import java.util.List;

public class NotesCommand extends GetNotesCommand {
	@Override
	protected String getRequiredPermissions() {
		return "note";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		CommandTarget target = getTarget(event);
		List<String> notes = List.of("An unknown error has occurred");
		if(target == null)
			notes = List.of(String.format("Usage is `%snotes <ckey or @Username>`", Yogbot.config.discordConfig.commandPrefix));
		else {
			if(target.getCkey() == null) {
				String populateResult = target.populate();
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
