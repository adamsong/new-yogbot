package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.PartialMember;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.util.MonoCollector;
import reactor.core.publisher.Mono;

import java.util.List;

public class NotesCommand extends GetNotesCommand {
	@Override
	protected String getRequiredPermissions() {
		return "note";
	}

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		List<PartialMember> partialMembers;
		String[] args;
		List<String> notes;
		if((partialMembers = event.getMessage().getMemberMentions()).size() > 0) {
			notes = getNotes(partialMembers.get(0).getId().asLong(), true);
		} else if((args = event.getMessage().getContent().split(" ")).length > 1) {
			notes = getNotes(args[1], true);
		} else {
			notes = List.of(String.format("Usage is `%snotes <ckey or @Username>`", Yogbot.config.discordConfig.commandPrefix));
		}
		return event.getMessage().getChannel().flatMap(messageChannel ->
			notes.stream().map(messageChannel::createMessage).collect(MonoCollector.toMono()));
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
