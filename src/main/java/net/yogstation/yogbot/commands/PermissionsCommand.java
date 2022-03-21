package net.yogstation.yogbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import net.yogstation.yogbot.config.DiscordConfig;
import net.yogstation.yogbot.permissions.PermissionsManager;
import reactor.core.publisher.Mono;


public abstract class PermissionsCommand extends TextCommand {

	protected final PermissionsManager permissions;
	
	public PermissionsCommand(DiscordConfig discordConfig, PermissionsManager permissions) {
		super(discordConfig);
		this.permissions = permissions;
	}
	
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
		if(getRequiredPermissions() == null) return true;
		return permissions.hasPermission(member, getRequiredPermissions());
	}

}
