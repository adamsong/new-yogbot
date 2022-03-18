package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.Yogbot;
import net.yogstation.yogbot.permissions.PermissionsNode;
import reactor.core.publisher.Mono;

public abstract class PermissionsCommand extends TextCommand {
	protected final String requiredPermission;

	public PermissionsCommand(String requiredPermission) {
		this.requiredPermission = requiredPermission;
	}

	@Override
	protected boolean canFire(MessageCreateEvent event) {
		return Yogbot.permissions.hasPermission(event.getMember().orElse(null), requiredPermission);
	}

	@Override
	protected Mono<?> doError(MessageCreateEvent event) {
		return reply(event, "You do not have permission to use this command");
	}

}
