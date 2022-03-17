package net.yogstation.yogbot.interactions;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import net.yogstation.yogbot.listeners.IEventHandler;

import java.util.Locale;

public interface IInteractionHandler<T extends ApplicationCommandInteractionEvent> extends IEventHandler<T> {
	default String getURI() {
		return getName().toLowerCase(Locale.ROOT) + ".json";
	}
}
