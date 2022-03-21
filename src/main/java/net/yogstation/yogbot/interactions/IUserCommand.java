package net.yogstation.yogbot.interactions;

import discord4j.core.event.domain.interaction.UserInteractionEvent;
import net.yogstation.yogbot.listeners.IEventHandler;

import java.util.Locale;

public interface IUserCommand extends IInteractionHandler, IEventHandler<UserInteractionEvent> {
	@Override
	default String getURI() {
		return getName().toLowerCase(Locale.ROOT) + ".json";
	}
}
