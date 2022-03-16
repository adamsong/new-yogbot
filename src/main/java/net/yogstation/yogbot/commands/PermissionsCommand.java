package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.Yogbot;
import reactor.core.publisher.Mono;

public abstract class PermissionsCommand {
	protected final String requiredPermission;

	public PermissionsCommand(String requiredPermission) {
		this.requiredPermission = requiredPermission;
	}

	/**
	 * Checks if the application command is being run by someone authorized to run the command
	 * @param event The interaction event
	 * @return If the member has permission
	 */
	protected boolean hasPermission(ApplicationCommandInteractionEvent event) {
		Member member = event.getInteraction().getMember().orElse(null);
		if(member == null) return false;

		return member.getRoles()
				.any(role -> Yogbot.permissions.getNodeFor(role.getName()).hasPermission(requiredPermission))
				.block() == Boolean.TRUE; // == Boolean.TRUE prevents NPE
	}

	/**
	 * Replies with a standard permissions error
	 * @param event The event to reply to
	 * @return The reply
	 */
	protected Mono<Void> permissionError(ApplicationCommandInteractionEvent event) {
		return event.reply().withEphemeral(true).withContent("You do not have permission to run this command.");
	}
}
