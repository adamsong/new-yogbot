package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import net.yogstation.yogbot.util.MonoCollector;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class MyNotesCommand extends GetNotesCommand {

	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Optional<User> author = event.getMessage().getAuthor();
		if(author.isEmpty()) return Mono.empty();
		long id = author.get().getId().asLong();
		return author.get().getPrivateChannel().flatMap(privateChannel ->
			getNotes(id, false).stream().map(privateChannel::createMessage).collect(MonoCollector.toMono()));
	}

	@Override
	protected String getDescription() {
		return "Checks your notes";
	}

	@Override
	public String getName() {
		return "mynotes";
	}

	@Override
	protected String getRequiredPermissions() {
		return null;
	}
}
