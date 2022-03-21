package net.yogstation.yogbot.interactions;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import net.yogstation.yogbot.listeners.IEventHandler;

import java.util.Locale;

public interface IInteractionHandler {
	String getURI();
}
