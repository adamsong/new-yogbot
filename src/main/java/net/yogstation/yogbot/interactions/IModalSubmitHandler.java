package net.yogstation.yogbot.interactions;

import discord4j.core.event.domain.interaction.ModalSubmitInteractionEvent;
import reactor.core.publisher.Mono;

public interface IModalSubmitHandler {
	String getIdPrefix();
	Mono<?> handle(ModalSubmitInteractionEvent event);
}
