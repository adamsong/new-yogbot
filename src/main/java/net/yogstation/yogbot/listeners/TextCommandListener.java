package net.yogstation.yogbot.listeners;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.commands.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class TextCommandListener {
	private static final List<TextCommand> commands = new ArrayList<>();
	
	static {
		commands.add(new ActivityCommand());
		commands.add(new AddAOCommand());
		commands.add(new AddMentorCommand());
		commands.add(new AshCommand());
		commands.add(new AdminWhoCommand());
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
		commands.add(new HelpCommand());
		commands.add(new InfoCommand());
		commands.add(new JamieCommand());
		commands.add(new KMCCommand());
		commands.add(new KtlwjecCommand());
		commands.add(new ListAdminsCommand());
		commands.add(new ListMentorsCommand());
		commands.add(new LizardCommand());
		commands.add(new LockerCommand());
		commands.add(new LoreBanCommand());
		commands.add(new MHelpCommand());
		commands.add(new MentorBanCommand());
		commands.add(new MojaCommand());
		commands.add(new MyIDCommand());
		commands.add(new MyNotesCommand());
		commands.add(new NiclasCommand());
		commands.add(new NotesCommand());
		commands.add(new PingCommand());
		commands.add(new RemoveAOCommand());
		commands.add(new RemoveMentorCommand());
		commands.add(new ReviewCommand());
		commands.add(new SubscribeCommand());
		commands.add(new TicketCommand());
		commands.add(new ToggleOOCCommand());
		commands.add(new UnlinkCommand());
		commands.add(new UnsubscribeCommand());
		commands.add(new UserverifyCommand());
	}

	public static List<String> getHelpMessages(Member member, boolean hidden) {
		List<String> messages = new ArrayList<>();
		for(TextCommand command : commands) {
			if(command.isHidden() != hidden) continue;
			if(command instanceof PermissionsCommand && !((PermissionsCommand) command).hasPermission(member))
				continue;
			messages.add(command.getHelpText());
		}
		return messages;
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
