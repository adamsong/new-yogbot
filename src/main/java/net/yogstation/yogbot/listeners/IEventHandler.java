package net.yogstation.yogbot.listeners;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface IEventHandler<T extends Event> {
    /**
     * Gets the name of the interaction as sent by discord
     * @return the name
     */
    String getName();

    /**
     * This is the method that is actuallhy called when the interaction actually triggers
     * @param event The inciting event
     * @return The Mono with reply information
     */
    Mono<?> handle(T event);
}
