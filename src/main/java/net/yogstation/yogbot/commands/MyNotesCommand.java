package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import net.yogstation.yogbot.DatabaseManager;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import net.yogstation.yogbot.util.ByondLinkUtil;
import net.yogstation.yogbot.util.MonoCollector;
import net.yogstation.yogbot.util.Result;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class MyNotesCommand extends GetNotesCommand {
	
	public MyNotesCommand(DiscordConfig discordConfig, PermissionsManager permissions, DatabaseManager database) {
		super(discordConfig, permissions, database);
	}
	
	@Override
	protected Mono<?> doCommand(MessageCreateEvent event) {
		Optional<User> author = event.getMessage().getAuthor();
		if(author.isEmpty()) return Mono.empty();
		Result<String, String> ckeyResult = ByondLinkUtil.getCkey(author.get().getId(), database);
		if(ckeyResult.hasError()) return reply(event, ckeyResult.getError());
		return author.get().getPrivateChannel().flatMap(privateChannel ->
			getNotes(ckeyResult.getValue(), false).stream().map(privateChannel::createMessage).collect(MonoCollector.toMono()));
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
