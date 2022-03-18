package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;


public abstract class PermissionsCommand extends TextCommand {

	protected abstract String getRequiredPermissions();

	@Override
	protected boolean canFire(MessageCreateEvent event) {
		return hasPermission(event.getMember().orElse(null));
	}

	@Override
	protected Mono<?> doError(MessageCreateEvent event) {
		return reply(event, "You do not have permission to use this command");
	}

	public boolean hasPermission(Member member) {
		return Yogbot.permissions.hasPermission(member, getRequiredPermissions());
	}

}
