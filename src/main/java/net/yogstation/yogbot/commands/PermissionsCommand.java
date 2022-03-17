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
		return hasPermission(event.getMember().orElse(null));
	}

	@Override
	protected Mono<?> doError(MessageCreateEvent event) {
		return reply(event, "You do not have permission to use this command");
	}

	/**
	 * Checks if the application command is being run by someone authorized to run the command
	 * @param member The member
	 * @return If the member has permission
	 */
	protected boolean hasPermission(Member member) {
		if(member == null) return false;

		return member.getRoles()
				.any(role -> {
					PermissionsNode node = Yogbot.permissions.getNodeFor(role.getName());
					if(node == null) return false;
					return node.hasPermission(requiredPermission);
				})
				.block() == Boolean.TRUE; // == Boolean.TRUE prevents NPE
	}

}
